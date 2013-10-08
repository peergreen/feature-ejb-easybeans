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
package com.peergreen.ejb.easybeans.embedded;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.transaction.TransactionManager;
import javax.transaction.UserTransaction;

import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Instantiate;
import org.apache.felix.ipojo.annotations.Invalidate;
import org.apache.felix.ipojo.annotations.Provides;
import org.apache.felix.ipojo.annotations.Requires;
import org.apache.felix.ipojo.annotations.Validate;
import org.osgi.framework.BundleContext;
import org.osgi.service.jndi.JNDIContextManager;
import org.ow2.easybeans.api.EZBServer;
import org.ow2.easybeans.jmx.MBeansHelper;
import org.ow2.easybeans.naming.NamingManager;
import org.ow2.easybeans.naming.interceptors.ENCManager;
import org.ow2.easybeans.osgi.archive.BundleArchiveFactory;
import org.ow2.easybeans.osgi.extension.EasyBeansOSGiExtension;
import org.ow2.easybeans.osgi.extension.OSGiBindingFactory;
import org.ow2.easybeans.proxy.binding.BindingManager;
import org.ow2.easybeans.server.Embedded;
import org.ow2.easybeans.server.EmbeddedConfigurator;
import org.ow2.easybeans.server.EmbeddedException;
import org.ow2.easybeans.transaction.JTransactionManager;
import org.ow2.util.archive.impl.ArchiveManager;
import org.ow2.util.event.api.IEventService;
import org.ow2.util.jmx.api.ICommonsModelerExtService;

import com.peergreen.ejb.easybeans.embedded.naming.PeergreenENCInterceptor;

/**
 * Create a customized Embedded component and register it.
 * @author Florent Benoit
 */
@Component
@Instantiate
@Provides(specifications={Embedded.class, EZBServer.class})
public class PeergreenEasyBeansEmbedded extends Embedded {

    /**
     * Path of the configuration file
     */
    private static final String CONFIG_FILE = "/easybeans-config.xml";

    /**
     * Event service.
     */
    @Requires
    private IEventService eventService;

    /**
     * Commons modeler extension service.
     */
    @Requires
    private final ICommonsModelerExtService commonsModelerExtService = null;

    /**
     * JNDI context manager.
     */
    @Requires
    private JNDIContextManager jndiContextManager;

    /**
     * Transaction Manager
     */
    @Requires
    private TransactionManager transactionManager;

    /**
     * User Transaction
     */
    @Requires
    private UserTransaction userTransaction;

    /**
     * New InitialContext.
     */
    private Context initialContext;

    /**
     * Bundle context.
     */
    private final BundleContext bundleContext;

    /**
     * Default constructor.
     */
    public PeergreenEasyBeansEmbedded(BundleContext bundleContext) {
        this.bundleContext = bundleContext;
    }


    @Override
    @Validate
    public void start()  throws EmbeddedException {
        ClassLoader old = Thread.currentThread().getContextClassLoader();
        try {
            Thread.currentThread().setContextClassLoader(PeergreenEasyBeansEmbedded.class.getClassLoader());

            // Creates a new InitialContext
            try {
                this.initialContext = jndiContextManager.newInitialContext();
            } catch (NamingException e) {
                throw new EmbeddedException("Unable to get InitialContext", e);
            }

            // Sets the transaction manager
            JTransactionManager.setTransactionManager(transactionManager);

            // sets User Transaction
            try {
                NamingManager.getInstance().setUserTransaction(userTransaction);
            } catch (NamingException e) {
                throw new EmbeddedException("Unable to set user transaction", e);
            }


            // Sets the initial context
            super.setInitialContext(initialContext);
            super.setCorbaCompliant(false);

            // Load configuration file
            URL configFile = PeergreenEasyBeansEmbedded.class.getResource(CONFIG_FILE);


            // EasyBeans should use the JOnAS domain name and server name
            MBeansHelper.setDomainName("peergreen");
            MBeansHelper.setServerName("peergreen");

            ENCManager.setInterceptorClass(PeergreenENCInterceptor.class);

            //
            //MDBResourceAdapterHelper.setResourceAdapterFinder(resourceAdapterFinder);

            //
            //ENCManager.setInterceptorClass(JOnASENCInterceptor.class);
            //SecurityCurrent.setSecurityCurrent(new JOnASSecurityCurrent());




            // Sets the Management Pool
            //ManagementPool managementPool = new ManagementPool(ReusableThreadPoolFactory.createWorkManagerThreadPool(workManagerService.getWorkManager(), MANAGEMENTPOOL_WORKMANAGER_LIMIT));
            //embedded.setManagementThreadPool(managementPool);


            // Provides a Map of instances to be injected
            final Map<String, Object> context = new HashMap<String, Object>();
            context.put("event-service", this.eventService);
            context.put("modeler-service", this.commonsModelerExtService);


            final List<URL> configurations = new ArrayList<>();
            // Load core components ...
            URL coreComponentsURL = Embedded.class.getResource("easybeans-core.xml");
            configurations.add(coreComponentsURL);
            // ... and the JOnAS XML configuration
            configurations.add(configFile);

            EmbeddedConfigurator.init(this, configurations, context);

            // Do not load the easybeans' core components during startup  (already added)
            getServerConfig().setAddEmbeddedComponents(false);

            registerExtension();

            super.start();
        } catch (EmbeddedException e) {
            e.printStackTrace();
        } finally {
            Thread.currentThread().setContextClassLoader(old);
        }

    }

    /**
     * FIXME : This should be done through OSGi services
     */
    protected void registerExtension() {

        // Add extension factory
        EasyBeansOSGiExtension extension = new EasyBeansOSGiExtension();
        extension.setBundleContext(bundleContext);
        getServerConfig().addExtensionFactory(extension);

        ArchiveManager am = ArchiveManager.getInstance();
        BundleArchiveFactory bundleArchiveFactory = new BundleArchiveFactory();
        am.addFactory(bundleArchiveFactory);

        // Add OSGi BF
        // The EjbJars will now be exposed as OSGi services
        OSGiBindingFactory bindingFactory = new OSGiBindingFactory();
        BindingManager.getInstance().registerFactory(bindingFactory);

    }



    @Override
    @Invalidate
    public void stop() throws EmbeddedException {
        super.stop();
    }

}
