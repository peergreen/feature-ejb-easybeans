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
package com.peergreen.ejb.easybeans.facet;

import org.ow2.easybeans.api.EZBContainer;
import org.ow2.util.ee.deploy.api.deployable.EJB3Deployable;

import com.peergreen.ejb.easybeans.EJB3;

public class DefaultEJB3 implements EJB3 {

    private EJB3Deployable ejb3Deployable;

    private EZBContainer container;

    @Override
    public EJB3Deployable getDeployable() {
        return ejb3Deployable;
    }

    @Override
    public void setDeployable(EJB3Deployable deployable) {
        this.ejb3Deployable = deployable;
    }

    @Override
    public EZBContainer getContainer() {
        return container;
    }

    @Override
    public void setContainer(EZBContainer container) {
        this.container = container;
    }

}
