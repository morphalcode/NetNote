/*
 * Copyright 2021 Delft University of Technology
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package client;

import client.scenes.*;
import client.services.*;
import client.utils.ServerUtils;
import client.webSocket.WebSocketManager;
import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.Scopes;

public class MyModule implements Module {

    /**
     * @param binder binder
     */
    @Override
    public void configure(Binder binder) {
        binder.bind(InterfaceCtrl.class).in(Scopes.SINGLETON);
        binder.bind(Config.class).in(Scopes.SINGLETON);
        binder.bind(ServerUtils.class).in(Scopes.SINGLETON);
        binder.bind(WebSocketManager.class).in(Scopes.SINGLETON);
        binder.bind(InterfaceService.class).in(Scopes.SINGLETON);
        binder.bind(VarService.class).in(Scopes.SINGLETON);
        binder.bind(CollectionsMenuService.class).in(Scopes.SINGLETON);
        binder.bind(CurrentNoteAreaService.class).in(Scopes.SINGLETON);
        binder.bind(FilesSectionService.class).in(Scopes.SINGLETON);
        binder.bind(MainButtonsService.class).in(Scopes.SINGLETON);
        binder.bind(SearchBarService.class).in(Scopes.SINGLETON);
        binder.bind(TitleListService.class).in(Scopes.SINGLETON);
        binder.bind(CollectionInterfaceCtrl.class).in(Scopes.SINGLETON);
    }
}