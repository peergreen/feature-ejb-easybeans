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
package com.peergreen.ejb.easybeans.lifecycle;

import java.util.ArrayList;
import java.util.List;

import com.peergreen.deployment.DeploymentMode;
import com.peergreen.deployment.FacetLifeCyclePhaseProvider;
import com.peergreen.ejb.easybeans.EJB3;

public class EJBLifeCyclePhaseProvider implements FacetLifeCyclePhaseProvider<EJB3> {

    private final List<String> deployPhases;
    private final List<String> updatePhases;
    private final List<String> undeployPhases;

    public EJBLifeCyclePhaseProvider() {
        this.deployPhases = new ArrayList<String>();
        deployPhases.add("EJB3_START");

        this.undeployPhases = new ArrayList<String>();
        undeployPhases.add("EJB3_STOP");

        this.updatePhases = new ArrayList<String>();
        updatePhases.addAll(undeployPhases);
        updatePhases.addAll(deployPhases);
    }

    @Override
    public List<String> getLifeCyclePhases(DeploymentMode deploymentMode) {
        switch (deploymentMode) {
            case DEPLOY:
                return deployPhases;
            case UPDATE:
                return updatePhases;
            case UNDEPLOY:
                return undeployPhases;
                default : throw new IllegalStateException("Deployment mode '" + deploymentMode + "' not supported");
        }
    }

}
