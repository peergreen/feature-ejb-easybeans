/**
 * Copyright 2013 Peergreen S.A.S. All rights reserved.
 * Proprietary and confidential.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.peergreen.ejb.easybeans.processor;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javax.naming.NamingException;

import org.apache.felix.ipojo.annotations.Requires;
import org.ow2.easybeans.api.EZBContainer;
import org.ow2.easybeans.api.EZBContainerException;
import org.ow2.easybeans.api.EZBServer;
import org.ow2.easybeans.deployment.EasyBeansDeployableInfo;
import org.ow2.easybeans.deployment.api.EZBDeployableInfo;
import org.ow2.easybeans.ejbinwar.EasyBeansEJBWarBuilder;
import org.ow2.easybeans.resolver.ApplicationJNDIResolver;
import org.ow2.easybeans.resolver.api.EZBApplicationJNDIResolver;
import org.ow2.easybeans.resolver.api.EZBContainerJNDIResolver;
import org.ow2.util.archive.api.IArchive;
import org.ow2.util.archive.api.IArchiveManager;
import org.ow2.util.ee.deploy.api.deployable.EJB3Deployable;
import org.ow2.util.ee.deploy.api.deployable.IDeployable;
import org.ow2.util.ee.deploy.api.deployable.WARDeployable;
import org.ow2.util.ee.deploy.api.helper.DeployableHelperException;
import org.ow2.util.ee.deploy.api.helper.IDeployableHelper;
import org.ow2.util.ee.deploy.impl.helper.DeployableHelper;

import com.peergreen.deployment.ProcessorContext;
import com.peergreen.deployment.ProcessorException;
import com.peergreen.deployment.processor.Phase;
import com.peergreen.deployment.processor.Processor;
import com.peergreen.ejb.easybeans.EJB3;
import com.peergreen.ejb.easybeans.facet.DefaultEJB3;
import com.peergreen.webcontainer.WebApplication;

/**
 * Check if the webapp is containing EJB3s.
 * @author Florent Benoit
 */
@Processor
@Phase("METADATA")
public class EJBInWarProcessor {

    @Requires
    private IDeployableHelper deployableHelper;

    @Requires
    private EZBServer embedded;

    @Requires
    private IArchiveManager archiveManager;


    /**
     * Start.
     */
    public void handle(WebApplication webApplication, ProcessorContext processorContext) throws ProcessorException {

        Map<Object, Object> properties = new HashMap<Object, Object>();
        properties.put(EZBApplicationJNDIResolver.class, new ApplicationJNDIResolver());
        properties.put(ClassLoader.class, webApplication.getClassLoader());
        properties.put("application.name", webApplication.getApplicationName());
        properties.put("module.name", webApplication.getModuleName());
        properties.put("application.context", webApplication.getJavaAppContext());
        try {
            properties.put("env.context", webApplication.getJavaContext().lookup("comp/env"));
        } catch (NamingException e) {
            throw new ProcessorException("Unable to get comp/env subcontext", e);
        }
        properties.put("module.context", webApplication.getJavaModuleContext());

        // Check if there are EJBs inside the war
        EZBContainer ejb3InWarContainer = getEJBContainerFromWar(properties, processorContext);

        // We've found an EJB3 deployable, enhance it
        if (ejb3InWarContainer != null) {
            ClassLoader old = Thread.currentThread().getContextClassLoader();
            Thread.currentThread().setContextClassLoader(EJBInWarProcessor.class.getClassLoader());
            try {
                ejb3InWarContainer.resolve();
                //ejb3InWarContainer.setPersistenceUnitManager(ezbInjectionHolder.getPersistenceUnitManager());
                ejb3InWarContainer.enhance(true);
            } catch (EZBContainerException e) {
                throw new ProcessorException("Cannot deploy deployable", e);
            } finally {
                Thread.currentThread().setContextClassLoader(old);
            }

        }
    }

    public EZBContainer getEJBContainerFromWar(Map<Object, Object> properties, ProcessorContext processorContext) throws ProcessorException {
        EZBContainer container = null;
        EasyBeansEJBWarBuilder builder = new EasyBeansEJBWarBuilder();
        builder.setDeployableHelper(deployableHelper);


        // We'll scan class with ASM in order to check classes
        IArchive ow2Archive = null;
        ow2Archive = archiveManager.getArchive(new File(processorContext.getArtifact().uri()));

        IDeployable<?> deployable = null;
        try {
            deployable = DeployableHelper.getDeployable(ow2Archive);
        } catch (DeployableHelperException e) {
            throw new ProcessorException("Unable to get deployable", e);
        }

        if (!(deployable instanceof WARDeployable)) {
            return null;
        }

        WARDeployable warDeployable = (WARDeployable) deployable;



        EJB3Deployable ejb3Deployable = builder.getEJBFromWarDeployable(warDeployable, properties);
        if (ejb3Deployable == null) {
            return null;
        }
        container = embedded.createContainer(ejb3Deployable);

        DefaultEJB3 ejb3 = new DefaultEJB3();
        ejb3.setContainer(container);
        ejb3.setDeployable(ejb3Deployable);
        processorContext.addFacet(EJB3.class, ejb3);

        EZBDeployableInfo deployableInfo = (EZBDeployableInfo) ejb3Deployable.getExtension(EasyBeansDeployableInfo.class);
        ClassLoader ejb3ClassLoader = null;

        EZBApplicationJNDIResolver applicationJNDIResolver = null;
        if (deployableInfo != null) {
            ejb3ClassLoader = deployableInfo.getClassLoader();
            applicationJNDIResolver = deployableInfo.getApplicationJNDIResolver();
            if (ejb3ClassLoader != null) {
                container.setClassLoader(ejb3ClassLoader);
            }
            if (applicationJNDIResolver != null) {
                EZBContainerJNDIResolver containerJNDIResolver = container.getConfiguration().getContainerJNDIResolver();
                containerJNDIResolver.setApplicationJNDIResolver(applicationJNDIResolver);
                // Add child on application JNDI Resolver
                applicationJNDIResolver.addContainerJNDIResolver(containerJNDIResolver);
            }
        }
        return container;
    }


}
