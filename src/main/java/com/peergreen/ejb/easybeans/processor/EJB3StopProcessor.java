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
package com.peergreen.ejb.easybeans.processor;

import com.peergreen.deployment.ProcessorContext;
import com.peergreen.deployment.ProcessorException;
import com.peergreen.deployment.processor.Phase;
import com.peergreen.deployment.processor.Processor;
import com.peergreen.ejb.easybeans.EJB3;

/**
 * Perform actions while stopping
 * @author Florent Benoit
 */
@Processor
@Phase("EJB3_STOP")
public class EJB3StopProcessor {

    /**
     * Stop
     */
    public void handle(EJB3 ejb3, ProcessorContext processorContext) throws ProcessorException {
        ejb3.getContainer().stop();
    }

}
