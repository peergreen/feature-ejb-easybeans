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
package com.peergreen.ejb.easybeans.embedded.management;

import org.ow2.easybeans.jsr77.JSR77ManagementIdentifier;
import org.ow2.easybeans.server.Embedded;

/**
 * Generates an ObjectName for the {@link Embedded} component.
 * @author Florent Benoit
 */
public class PeergreenEasyBeansEmbeddedIdentifier extends JSR77ManagementIdentifier<Embedded> {

    /**
     * JMX MBean Type.
     */
    private static final String TYPE = "J2EEServer";

    /**
     * {@inheritDoc}
     */
    @Override
    public String getAdditionnalProperties(final Embedded instance) {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getNamePropertyValue(final Embedded instance) {
        // If ServerName is already set, return this value
        if (getServerName() != null) {
            return getServerName();
        }
        return "EasyBeans_" + instance.getID();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getTypeValue() {
        return TYPE;
    }

}
