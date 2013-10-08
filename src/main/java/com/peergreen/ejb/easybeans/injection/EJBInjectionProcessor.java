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
package com.peergreen.ejb.easybeans.injection;

import javax.naming.NamingException;

import org.ow2.easybeans.api.EZBServer;
import org.ow2.easybeans.deployment.EasyBeansDeployableInfo;
import org.ow2.easybeans.deployment.api.EZBDeployableInfo;
import org.ow2.easybeans.resolver.api.EZBApplicationJNDIResolver;
import org.ow2.easybeans.resolver.api.EZBJNDIResolverException;
import org.ow2.easybeans.resolver.api.EZBServerJNDIResolver;
import org.ow2.util.ee.deploy.api.deployable.EJB3Deployable;
import org.ow2.util.ee.metadata.common.api.struct.IJEjbEJB;
import org.ow2.util.ee.metadata.common.api.view.ICommonView;
import org.ow2.util.log.Log;
import org.ow2.util.log.LogFactory;
import org.ow2.util.scan.api.metadata.IFieldMetadata;
import org.ow2.util.scan.api.metadata.IMetadata;
import org.ow2.util.scan.api.metadata.IMethodMetadata;

import com.peergreen.deployment.Artifact;
import com.peergreen.ejb.easybeans.EJB3;
import com.peergreen.metadata.adapter.Binding;
import com.peergreen.metadata.adapter.InjectionContext;
import com.peergreen.metadata.adapter.InjectionProcessor;

@InjectionProcessor("javax.ejb.EJB")
public class EJBInjectionProcessor {

    private final EZBServer embedded;

    private final Log logger = LogFactory.getLog(EJBInjectionProcessor.class);

    public EJBInjectionProcessor(EZBServer embedded) {
        this.embedded = embedded;
    }

    public Binding<Object> handle(InjectionContext injectionContext) {

        // Get artifact
        Artifact artifact = injectionContext.getArtifact();

        ClassLoader old = Thread.currentThread().getContextClassLoader();
        try {
            EJB3 ejb3 = artifact.as(EJB3.class);
            Thread.currentThread().setContextClassLoader(ejb3.getContainer().getClassLoader());

            // TODO: Try to see if there is a local JNDI Resolver

            // Get global JNDI resolver
            EZBServerJNDIResolver jndiResolver = embedded.getJNDIResolver();

            // Get metadata
            IMetadata metadata = injectionContext.getMetadata();

            String interfaceName = null;
            if (metadata instanceof IFieldMetadata) {
                IFieldMetadata fieldMetadata = (IFieldMetadata) metadata;
                interfaceName = fieldMetadata.getType();
            } else if (metadata instanceof IMethodMetadata) {
                IMethodMetadata methodMetadata = (IMethodMetadata) metadata;
                String[] params = methodMetadata.getParametersClassName();
                if (params.length == 1) {
                    interfaceName = params[0];
                }
            }

            // get javax.ejb.EJB data
            ICommonView commonView = metadata.as(ICommonView.class);
            IJEjbEJB jEjb = commonView.getJEjbEJB();

            if (jEjb == null) {
                logger.error("Injection processor called for javax.ejb.EJB but no annotation found");
                return null;
            }

            // JNDI name
            String jndiName = jEjb.getLookup();
            if (jndiName == null) {

                // ejbName ?
                String beanName = jEjb.getBeanName();


                // Mapped Name ? if not null, use it as JNDI name
                String mappedName = jEjb.getMappedName();
                if (mappedName != null && !mappedName.equals("")) {
                    jndiName = mappedName;
                }

                if (jndiName == null) {
                    // Try on the container resolver

                    EJB3Deployable ejb3Deployable = ejb3.getDeployable();
                    EZBDeployableInfo deployableInfo = (EZBDeployableInfo) ejb3Deployable.getExtension(EasyBeansDeployableInfo.class);
                    EZBApplicationJNDIResolver applicationJNDIResolver = deployableInfo.getApplicationJNDIResolver();

                    try {
                        jndiName = applicationJNDIResolver.getEJBJNDIUniqueName(interfaceName, beanName);
                    } catch (EZBJNDIResolverException e) {
                        logger.error("No jndi name found on class {0} for interface {1} and beanName {2}",
                                metadata.getMember().getName(), interfaceName, beanName);
                    }
                }
            }

            Binding<Object> binding = null;

            // JNDI name not null
            if (jndiName != null) {
                Object ejb = null;
                try {
                    ejb = embedded.getInitialContext().lookup(jndiName);
                } catch (NamingException e) {
                    logger.error("Unable to find reference", e);
                    return null;
                }
                binding = injectionContext.createBinding(jEjb.getName(), ejb);

            }

            return binding;

        } finally {
            Thread.currentThread().setContextClassLoader(old);
        }
    }

}
