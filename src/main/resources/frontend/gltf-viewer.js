import { LitElement, html, css } from 'lit';
import * as THREE from 'three';
import { GLTFLoader } from 'three/examples/jsm/loaders/GLTFLoader.js';
import { OrbitControls } from 'three/examples/jsm/controls/OrbitControls.js';

class GltfViewer extends LitElement {
  static get properties() {
    return {
      base64Data: { type: String },
    };
  }

  constructor() {
    super();
    this.base64Data = '';
    this.model = null;
  }

  static styles = css`
    :host {
      display: block;
      width: 100%;
      height: 100vh;
    }
  `;

  firstUpdated() {
    this.initializeThreeJS();
  }

  updated(changedProperties) {
    if (changedProperties.has('base64Data')) {
      console.log('Base64 Data Received:', this.base64Data);
      this.loadModel(this.base64Data);
    }
  }

  initializeThreeJS() {
    this.scene = new THREE.Scene();

    this.camera = new THREE.PerspectiveCamera(
      75,
      this.clientWidth / this.clientHeight,
      0.1,
      1000
    );
    this.camera.position.z = 5;

    this.renderer = new THREE.WebGLRenderer({ antialias: true });
    this.renderer.setSize(this.clientWidth, this.clientHeight);
    this.shadowRoot.appendChild(this.renderer.domElement);

    this.controls = new OrbitControls(this.camera, this.renderer.domElement);
    this.controls.target.set(0, 0, 0);
    this.controls.enableDamping = true;
    this.controls.dampingFactor = 0.05;
    this.controls.update();

    const ambientLight = new THREE.AmbientLight(0xffffff, 0.5);
    this.scene.add(ambientLight);

    const directionalLight = new THREE.DirectionalLight(0xffffff, 1);
    directionalLight.position.set(0, 10, 10);
    this.scene.add(directionalLight);

    this.animate();
  }

  animate() {
    requestAnimationFrame(() => this.animate());

    this.controls.update();

    this.renderer.render(this.scene, this.camera);
  }

  loadModel(base64Data) {
    if (!base64Data) {
      console.error('No Base64 Data Provided');
      return;
    }

    const loader = new GLTFLoader();

    const binaryString = atob(base64Data);
    const len = binaryString.length;
    const arrayBuffer = new ArrayBuffer(len);
    const uint8Array = new Uint8Array(arrayBuffer);
    for (let i = 0; i < len; i++) {
      uint8Array[i] = binaryString.charCodeAt(i);
    }

    loader.parse(
      arrayBuffer,
      '',
      (gltf) => {
        console.log('Model Loaded:', gltf);
        this.model = gltf.scene;

        this.model.scale.set(1, 1, 1);
        this.model.position.set(0, 0, 0);

        this.scene.add(this.model);
      },
      (error) => {
        console.error('Error Parsing Model:', error);
      }
    );
  }
}

customElements.define('gltf-viewer', GltfViewer);
