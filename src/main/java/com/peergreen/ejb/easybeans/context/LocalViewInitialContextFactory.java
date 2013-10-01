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
package com.peergreen.ejb.easybeans.context;

import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.spi.InitialContextFactory;

import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Instantiate;
import org.apache.felix.ipojo.annotations.Provides;
import org.apache.felix.ipojo.annotations.Requires;
import org.apache.felix.ipojo.annotations.Validate;

import com.peergreen.naming.ContextFactory;

/**
 * Binds a global JNDI context if no context is available
 * This allows to register object in global JNDI
 * @author Florent Benoit
 */
@Component
@Instantiate
@Provides
public class LocalViewInitialContextFactory implements InitialContextFactory {

    @Requires
    private ContextFactory contextFactory;

    private Context global;

    @Validate
    public void init() {
        this.global = contextFactory.createContext("global");
    }

    @Override
    public Context getInitialContext(Hashtable<?, ?> environment) throws NamingException {
       return global;
    }

}
