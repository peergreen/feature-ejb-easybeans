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

import javax.management.MBeanException;

import org.ow2.easybeans.jsr77.J2EEServerMBean;

/**
 * J2EEServer JSR77 MBean.
 * @author Florent Benoit
 */
public class PeergreenEasyBeansEmbeddedMBean extends J2EEServerMBean {

    /**
     * Creates a new Managed Object.
     * @throws MBeanException if creation fails.
     */
    public PeergreenEasyBeansEmbeddedMBean() throws MBeanException {
        super();
    }
}
