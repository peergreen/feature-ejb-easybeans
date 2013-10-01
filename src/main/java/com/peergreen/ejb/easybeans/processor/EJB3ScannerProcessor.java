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

import java.io.File;
import java.util.Map;

import org.apache.felix.ipojo.annotations.Requires;
import org.osgi.framework.Constants;
import org.ow2.util.archive.api.IArchive;
import org.ow2.util.archive.api.IArchiveManager;
import org.ow2.util.ee.deploy.api.deployable.EJB3Deployable;
import org.ow2.util.ee.deploy.api.deployable.IDeployable;
import org.ow2.util.ee.deploy.api.helper.DeployableHelperException;
import org.ow2.util.ee.deploy.impl.helper.DeployableHelper;

import com.peergreen.deployment.DiscoveryPhasesLifecycle;
import com.peergreen.deployment.ProcessorContext;
import com.peergreen.deployment.ProcessorException;
import com.peergreen.deployment.facet.archive.Archive;
import com.peergreen.deployment.facet.archive.ArchiveException;
import com.peergreen.deployment.processor.Discovery;
import com.peergreen.deployment.processor.Processor;
import com.peergreen.deployment.processor.Uri;
import com.peergreen.ejb.easybeans.EJB3;
import com.peergreen.ejb.easybeans.facet.DefaultEJB3;

/**
 * EJB3 scanner.
 * @author Florent Benoit
 */
@Processor
@Uri(extension = "jar")
@Discovery(DiscoveryPhasesLifecycle.FACET_SCANNER)
public class EJB3ScannerProcessor {

    private static final String EJB_JAR_XML = "META-INF/ejb-jar.xml";

    @Requires
    private IArchiveManager archiveManager;

    /**
     * If the archive is a jar file we try to see if there is EJBs inside
     */
    public void handle(Archive archive, ProcessorContext processorContext) throws ProcessorException {

        // It is a bundle ? exit
        Map<String, String> manifestEntries = archive.getManifestEntries();
        if (manifestEntries != null && manifestEntries.containsKey(Constants.BUNDLE_SYMBOLICNAME)) {
            return;
        }


        // Entry META-INF/ejb-jar.xml is in the jar ?
        //        try {
        //            if (archive.getResource(EJB_JAR_XML) != null) {
        //                addEJB3(archive, processorContext);
        //                return;
        //            }
        //        } catch (ArchiveException e) {
        //            throw new ProcessorException(String.format("Unable to check if %s if in the archive", EJB_JAR_XML), e);
        //        }



        // We'll scan class with ASM in order to check classes
        IArchive ow2Archive = null;
        try {
            ow2Archive = archiveManager.getArchive(new File(archive.getURI()));
        } catch (ArchiveException e) {
           throw new ProcessorException("Unable to build archive", e);
        }

        IDeployable<?> deployable = null;
        try {
            deployable = DeployableHelper.getDeployable(ow2Archive);
        } catch (DeployableHelperException e) {
            throw new ProcessorException("Unable to get deployable", e);
        }

        if (deployable instanceof EJB3Deployable) {
            addEJB3((EJB3Deployable) deployable, archive, processorContext);
        }
        // else do nothing

    }


    protected void addEJB3(EJB3Deployable ejb3Deployable, Archive archive, ProcessorContext processorContext) throws ProcessorException {
        // Adds the EJB3
        DefaultEJB3 ejb3 = new DefaultEJB3();

        ejb3.setDeployable(ejb3Deployable);

        // Add facet
        processorContext.addFacet(EJB3.class, ejb3);

    }




}
