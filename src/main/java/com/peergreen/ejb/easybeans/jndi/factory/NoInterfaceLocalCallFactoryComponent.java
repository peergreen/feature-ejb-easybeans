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
package com.peergreen.ejb.easybeans.jndi.factory;

import javax.naming.spi.ObjectFactory;

import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Instantiate;
import org.apache.felix.ipojo.annotations.Provides;
import org.ow2.easybeans.proxy.factory.NoInterfaceLocalCallFactory;

/**
 *
 * @author Florent Benoit
 */
@Component
@Instantiate
@Provides(specifications={ObjectFactory.class, NoInterfaceLocalCallFactory.class})
public class NoInterfaceLocalCallFactoryComponent extends NoInterfaceLocalCallFactory {

}
