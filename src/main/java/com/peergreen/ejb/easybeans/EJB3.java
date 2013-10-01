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
package com.peergreen.ejb.easybeans;

import org.ow2.easybeans.api.EZBContainer;
import org.ow2.util.ee.deploy.api.deployable.EJB3Deployable;

public interface EJB3 {

    EJB3Deployable getDeployable();

    void setDeployable(EJB3Deployable deployable);

    EZBContainer getContainer();

    void setContainer(EZBContainer container);
}
