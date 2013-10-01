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

import javax.ejb.EJB;

import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Instantiate;
import org.apache.felix.ipojo.annotations.Provides;
import org.apache.felix.ipojo.annotations.Requires;
import org.ow2.easybeans.api.EZBServer;

import com.peergreen.metadata.adapter.Binding;
import com.peergreen.metadata.adapter.HandlerInjectionProcessor;
import com.peergreen.metadata.adapter.InjectionContext;

@Component
@Instantiate
@Provides
public class EJBHandlerInjectionProcessor implements HandlerInjectionProcessor {

    @Requires
    private EZBServer embedded;


    private final EJBInjectionProcessor wrapped;

    public EJBHandlerInjectionProcessor() {
        this.wrapped = new EJBInjectionProcessor(embedded);
    }

    @Override
    public String getAnnotation() {
        return EJB.class.getName();
    }

    @Override
    public Binding<?> handle(InjectionContext injectionContext) {
        return wrapped.handle(injectionContext);
    }

}
