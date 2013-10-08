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
package com.peergreen.ejb.easybeans.embedded.naming;

import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;
import org.ow2.easybeans.api.EasyBeansInvocationContext;
import org.ow2.easybeans.api.naming.NamingInterceptor;
import org.ow2.easybeans.naming.interceptors.AbsENCInterceptor;

import com.peergreen.naming.JavaNamingManager;

/**
 * Defines the naming interceptor for Peergreen JNDI naming
 * @author Florent Benoit
 */
public class PeergreenENCInterceptor extends AbsENCInterceptor implements NamingInterceptor {

    /**
     * the JNDI naming
     */
    private JavaNamingManager javaNamingManager = null;

    /**
     * Needs an empty constructor
     */
    public PeergreenENCInterceptor() {
        if (javaNamingManager == null) {
            // Gets the bundle context and then gets the service
            Bundle bundle = FrameworkUtil.getBundle(PeergreenENCInterceptor.class);
            ServiceReference<JavaNamingManager> javaNamingManagerRef = bundle.getBundleContext().getServiceReference(JavaNamingManager.class);
            javaNamingManager = bundle.getBundleContext().getService(javaNamingManagerRef);
        }
    }

    /**
     * Sets Peergreen invocation context
     * @param invocationContext context with useful attributes on the current
     *        invocation.
     * @return result of the next invocation (to chain interceptors).
     * @throws Exception needs for signature of interceptor.
     */
    @Override
    public Object intercept(final EasyBeansInvocationContext invocationContext) throws Exception {
        javaNamingManager.bindThreadContext(invocationContext.getFactory().getJavaContext());
        try {
            return invocationContext.proceed();
        } finally {
            javaNamingManager.unbindThreadContext();;
        }
    }
}
