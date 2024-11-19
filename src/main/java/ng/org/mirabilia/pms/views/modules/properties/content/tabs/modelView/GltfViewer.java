package ng.org.mirabilia.pms.views.modules.properties.content.tabs.modelView;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.dependency.NpmPackage;

import java.util.Base64;

@Tag("gltf-viewer")
@JsModule("./gltf-viewer.js")
@NpmPackage(value = "three", version = "^0.150.0")
@NpmPackage(value = "lit", version = "^2.6.1")
public class GltfViewer extends Component {
    public GltfViewer(byte[] gltfData) {
        String base64Data = Base64.getEncoder().encodeToString(gltfData);
        getElement().setProperty("base64Data", base64Data);
    }
}
