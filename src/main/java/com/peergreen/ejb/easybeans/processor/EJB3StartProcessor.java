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

import org.apache.felix.ipojo.annotations.Requires;
import org.ow2.easybeans.api.EZBContainer;
import org.ow2.easybeans.api.EZBContainerException;
import org.ow2.easybeans.api.EZBServer;

import com.peergreen.deployment.ProcessorContext;
import com.peergreen.deployment.ProcessorException;
import com.peergreen.deployment.processor.Phase;
import com.peergreen.deployment.processor.Processor;
import com.peergreen.ejb.easybeans.EJB3;

/**
 * Perform actions while starting
 * @author Florent Benoit
 */
@Processor
@Phase("EJB3_START")
public class EJB3StartProcessor {

    @Requires
    private EZBServer embedded;

    /**
     * Start.
     */
    public void handle(EJB3 ejb3, ProcessorContext processorContext) throws ProcessorException {
        ClassLoader old = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader(EJB3StartProcessor.class.getClassLoader());
        try {
            EZBContainer container = ejb3.getContainer();
            if (container == null) {
                container = embedded.createContainer(ejb3.getDeployable());
                ejb3.setContainer(container);
            }
            try {
                container.start();
            } catch (EZBContainerException e) {
                throw new ProcessorException("", e);
            }
        } finally {
            Thread.currentThread().setContextClassLoader(old);
        }
    }


}
