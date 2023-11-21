# Bevezetés
> Introduction

A KRTN-Brigád játékmotorja egy közepes komplexitású, Java nyelven készülő, LWJGL-t alkalmazó, Entity-Component System-re alapuló játékmotor. Ez a dokumentum azért felelős, hogy a megfelelő
diagramokkal szemléltesse az egyes részelemek funkcionalitásait.

# Kezdeti osztálydiagram
> Initial class diagram

![Initial class diagram]{scale-img}(./img/uml/initial.png)

## A kezdeti osztálydiagram elemzése
> Analysis of the initial class diagram

### Fő futtható osztály
> Main runnable class

A játékmotor maga úgy lesz használható, mint egy maven library, ebből kifolyólag a játékot futtató osztály
az adott projekt saját osztálya lesz, amelynek a `main` metódusában a játékmotor inicializálása és futtatása történik.

### Ablak osztály
> Window class

Az ablak osztály felelős azért, hogy a játék megjelenítéséhez szükséges ablakot létrehozza, és a játékfolyamat során a játékablak méretét változtatni tudja, tulajdonságait állíthassa (ablakcím, elhelyezkedés, teljes-képernyős mód, vertikális szinkronizáció, stb.), és a játékablakot a megfelelő OpenGL kontextussal rendelkező ablakra állítsa.

### Erőforrás kezelő osztály
> ResourceManager class

Az erőforrás kezelő osztály felelős azért, hogy a játékfolyamat során a játék által használt erőforrásokat (textúrák, modellek, hangok, stb.) betöltse, cache-elje, és használat után felszabadítsa.

### Entitás
> Entity

Az entitások a játékban megjelenő objektumokat reprezentálják. Az entitásokat komponensek alkotják, amelyek a különböző tulajdonságokat (pozíció, forgatás, skálázás, modell, szerep, stb.) reprezentálják.

### Komponens(ek)
> Component(s)

A komponensek a játékban megjelenő objektumok tulajdonságait reprezentálják. A komponensek egy entitáshoz tartoznak, és az entitásokat alkotják. A komponensek lehetnek például pozíciót, forgatást, skálázást, modellt, szerepet, stb. reprezentáló komponensek.

### Entitás gyár osztály
> EntityFactory class

Az entitás gyár osztály felelős azért, hogy az entitásokat létrehozza, és a megfelelő komponensekkel lássa el. A factory elrendezés minden beállítás és komponens létrehozás opcionális és sorrendtől független. Ezelől kivétel az olyan komponensek hozzáadása, amelyeknek a létrehozáshoz szükséges egy másik komponens is (pl. a `RendererComponent`-nek a `TransformComponent`-re van szüksége).

### Kollekció
> Collection

A kollekciók a játékban megjelenő objektumok csoportosítását reprezentálják. A kollekciók entitásokat tartalmaznak. A kollekciók lehetnek például játékosokat, ellenségeket, épületeket, stb. reprezentáló kollekciók.

### Sorosítható absztrakt osztály
> Serializable abstract class

A sorosítható absztrakt osztály felelős azért, hogy az ebből szármozó osztályokat sorosíthatóvá (menthetővé/betölthetővé) tegye. Ezt megfelelő absztrakt utasításokkal teszi lehetővé, amelyeket az ebből származó osztályoknak implementálniuk kell.

### Entitás menedzser osztály
> EntityManager class

Az entitás menedzser osztály felelős az entitások élettartamának és perszisztenciájának kezeléséért.
A menedszer egyik legfontosabb szerepe hogy elősegíti megfelelő cachelési stratégiák alkalmazásával a hatákony keresést az entitások között.

### Mentés menedzser osztály
> SaveManager class

A mentés menedzser osztály felelős azért, hogy a játékfolyamat során a játék állapotát elmentse, és a játék indításakor a mentett állapotot betöltse. Ez áll a megfelelő kollekciók alkalmazásából, a perszisztens azaz maradandó entitások és komponensek tárolásából, és a megfelelő sorosítási stratégiák alkalmazásából.

### Logika menedzser osztály
> LogicManager class

A logikai menedszer az ECS rendszer által alkalmazott logikai rendszerek felügyeletéért, futtatásáért és 
alkalmazásáért felelős osztály. A logikai rendszerek a játékfolyamat során a játéklogika megvalósításáért felelősek.

### Logika absztrakt osztály
> Logic abstract class

A logika absztrakt osztály felelős azért, hogy a játékfolyamat során a játéklogikát megvalósítsa. A logikai rendszerek ebből az osztályból származnak, és a megfelelő absztrakt metódusokat implementálják.

# Teljes osztálydiagram
> Full class diagram

![Full class diagram]{scale-img}(./img/uml/full.png)
[Teljesképernyős nézet](./img/uml/full.png)

## Osztályok felsorolása
> List of classes

Ebben a részben a jelenlegi (2023. 11. 21.)-es prototípusban szereplő osztályokat soroljuk fel, és röviden ismertetjük azokat.

### Fő futtható osztály
> Main runnable class

UML: {class} Main
UML: {+} main(args: {String[]}): {void} ({static})

A játékmotor maga úgy lesz használható, mint egy maven library, ebből kifolyólag a játékot futtató osztály
az adott projekt saját osztálya lesz, amelynek a `main` metódusában a játékmotor inicializálása és futtatása történik.

### Ablak osztály
> Window class

UML: {class} Window ({singleton})
UML: Fields:
UML: {-} instance: {Window} ({static})
UML: {-} windowHandle: {long}
UML: {-} logicThread: {Thread}
UML: {-} title: {String}
UML: {-} width: {int}
UML: {-} height: {int}
UML: {-} tickRate: {float}
UML: {-} vsync: {boolean}
UML: {-} fullscreen: {boolean}
UML: {-} msaa: {int}
UML: Methods:
UML: {-} constructor(width: {int}, height: {int}, title: {String}, tickRate: {float}, vsync: {boolean}, fullscreen: {boolean}, msaa: {int}): {Window}
UML: {+} initInstance(width: {int}, height: {int}, title: {String}, tickRate: {float}, vsync: {boolean}, fullscreen: {boolean}, msaa: {int}): {Window} ({static})
UML: {+} getInstance(): {Window} ({static})
UML: {+} init(): {void}
UML: {+} run(): {void}
UML: {-} loop(): {void}
UML: //* Setters and getters *

Az ablak osztály felelős azért, hogy a játék megjelenítéséhez szükséges ablakot létrehozza, és a játékfolyamat során a játékablak méretét változtatni tudja, tulajdonságait állíthassa (ablakcím, elhelyezkedés, teljes-képernyős mód, vertikális szinkronizáció, stb.), és a játékablakot a megfelelő OpenGL kontextussal rendelkező ablakra állítsa.

### Erőforrás kezelő osztály
> ResourceManager class

UML: {class} ResourceManager ({singleton})
UML: Fields:
UML: {-} INSTANCE: {ResourceManager} ({static})
UML: {-} shaderCache: {ResourceCache(Shader)} ({static})
UML: {-} textureCache: {ResourceCache(Texture)} ({static})
UML: {-} meshCache: {ResourceCache(StaticModelData)} ({static})
UML: Methods:
UML: {-} constructor(): {ResourceManager}
UML: {+} getInstance(): {ResourceManager} ({static})
UML: {+} readTextFile(path: {String}): {String}
UML: {+} readBinaryFile(path: {String}): {ByteBuffer}
UML: {+} loadShader(vertexShaderPath: {String}, fragmentShaderPath: {String}): {Shader}
UML: {+} loadTexture(path: {String}): {Texture}
UML: {+} loadStaticModel(path: {String}): {StaticModelData[]}
UML: {-} processMaterial(material: {AIMaterial}): {Material}
UML: {-} processMesh(mesh: {AIMesh}): {Mesh}

Az erőforrás kezelő osztály felelős azért, hogy a játékfolyamat során a játék által használt erőforrásokat (textúrák, modellek, hangok, stb.) betöltse, cache-elje, és használat után felszabadítsa.

### Erőforrás cache osztály
> ResourceCache class

UML: {class} ResourceCache<T>
UML: Fields:
UML: {-} resources: {HashMap(String, T)}
UML: Methods:
UML: {+} constructor(): {ResourceCache}
UML: {+} get(key: {String}): {T}
UML: {+} put(key: {String}, resource: {T}): {void}
UML: {+} has(key: {String}): {boolean}

Az erőforrás cache osztály felelős azért, hogy a játékfolyamat során a játék által használt erőforrásokat (textúrák, modellek, hangok, stb.) cache-elje, ezzel elkerülve a felesleges erőforrás betöltéseket.

### Statikus modell adat osztály
> StaticModelData class

UML: {class} StaticModelData
UML: Fields:
UML: {+} path: {String}
UML: {+} mesh: {Mesh}
UML: {+} material: {Material}
UML: Methods:
UML: {+} constructor(path: {String}, mesh: {Mesh}, material: {Material}): {StaticModelData}

A statikus modell adat osztály felelős azért, hogy a játékfolyamat során a játék által használt statikus modell adatokat (mesh, material, stb.) tárolja.

### Mesh osztály
> Mesh class

UML: {class} Mesh
UML: Fields:
UML: {-} vertexArrayObjectHandle: {int}
UML: {-} vertexBufferObjectHandle: {int}
UML: {-} elementBufferObjectHandle: {int}
UML: {-} vertexCount: {int}
UML: {-} indexCount: {int}
UML: Methods:
UML: {+} constructor(vertices: {float[]}, indices: {int[]}, layout: {MeshLayout}, drawType: {DrawTypes}): {Mesh}
UML: {+} bind(): {void}
UML: {+} unbind(): {void}
UML: {+} destroy(): {void}
UML: {+} getVertexCount(): {int}
UML: {+} getIndexCount(): {int}

A mesh osztály felelős azért, hogy a játékfolyamat során a játék által használt mesh-eket (azok vertexei, indexei, stb.) tárolja.

### Rajzolás típusok
> DrawTypes

UML: {enum} DrawTypes
UML: Values:
UML: {STATIC_DRAW}
UML: {DYNAMIC_DRAW}
UML: {STREAM_DRAW}

### Mesh elrendezés osztály
> MeshLayout class

UML: {class} MeshLayout
UML: Fields:
UML: {-} layout: {AttributeTypes[]}
UML: Methods:
UML: {+} constructor(layout: {AttributeTypes[]}): {MeshLayout}
UML: {+} getStride(): {int}
UML: {+} getAttributeAmount(): {int}
UML: {+} getAttributeSize(index: {int}): {int}
UML: {+} getAttributeAmount(index: {int}): {int}
UML: {+} getCountSum(): {int}

A mesh elrendezés osztály felelős azért, hogy a játékfolyamat során a játék által használt mesh-ek vertexeinek elrendezését tárolja.

### Attribútum típusok
> AttributeTypes

UML: {enum} AttributeTypes
UML: Values:
UML: {VEC4(Float.BYTES,4)}
UML: {VEC3(Float.BYTES,3)}
UML: {VEC2(Float.BYTES,2)}
UML: {FLOAT(Float.BYTES,1)}
UML: {SCALAR(Integer.BYTES,1)}
UML: {MAT4(Float.BYTES,16)}
UML: Methods:
UML: {+} constructor(size: {int}, count: {int}): {AttributeTypes}
UML: {+} getSize(): {int}
UML: {+} getCount(): {int}

Az attribútum típusok azért felelősek, hogy a mesh elrendezés osztályban a vertexek elrendezését ezekkel a típusokkal lehessen megadni.

### Anyag osztály
> Material class

UML: {class} Material
UML: Fields:
UML: {-} diffuseTexture: {Texture}
UML: {-} metallicTexture: {Texture}
UML: {-} roughnessTexture: {Texture}
UML: {-} ambientOcclusionTexture: {Texture}
UML: {-} emissionTexture: {Texture}
UML: Methods:
UML: {+} constructor(diffuseTexture: {Texture}, metallicTexture: {Texture}, roughnessTexture: {Texture}, ambientOcclusionTexture: {Texture}, emissionTexture: {Texture}): {Material}
UML: {+} getDiffuseTexture(): {Texture}
UML: {+} getMetallicTexture(): {Texture}
UML: {+} getRoughnessTexture(): {Texture}
UML: {+} getAmbientOcclusionTexture(): {Texture}
UML: {+} getEmissionTexture(): {Texture}
UML: {+} setDiffuseTexture(diffuseTexture: {Texture}): {void}
UML: {+} setMetallicTexture(metallicTexture: {Texture}): {void}
UML: {+} setRoughnessTexture(roughnessTexture: {Texture}): {void}
UML: {+} setAmbientOcclusionTexture(ambientOcclusionTexture: {Texture}): {void}
UML: {+} setEmissionTexture(emissionTexture: {Texture}): {void}

Az anyag osztály felelős azért, hogy a játékfolyamat során a játék által használt modellek anyagait tárolja.

### Textúra osztály
> Texture class

UML: {class} Texture
UML: Fields:
UML: {-} path: {String}
UML: {-} textureHandle: {int}
UML: {-} slots: {int[]} ({static})
UML: Methods:
UML: {+} constructor(color: {Vector4f}): {Texture}
UML: {+} constructor(data: {ByteBuffer}, width: {int}, height: {int}, path: {String}): {Texture}
UML: {+} constructor(data: {ByteBuffer}, width: {int}, height: {int}): {Texture}
UML: {-} initTexture(data: {ByteBuffer}, width: {int}, height: {int}): {void}
UML: {+} bind(slot: {int}): {void}
UML: {+} unbind(): {void}
UML: {+} destroy(): {void}
UML: {+} getPath(): {String}

A textúra osztály felelős azért, hogy a játékfolyamat során a játék által használt textúrákat tárolja.

### Shader osztály
> Shader class

UML: {class} Shader
UML: Fields:
UML: {-} activeShader: {int} ({static})
UML: {-} lightsCache: {List(Entity)} ({static})
UML: {-} compCache: {List(LightComponent)} ({static})
UML: {-} vertexShaderPath: {String}
UML: {-} fragmentShaderPath: {String}
UML: {-} handle: {int}
UML: {-} uniforms: {HashMap(String, Integer)}
UML: Methods:
UML: {+} constructor(vertexShader: {String}, fragmentShader: {String}, raw: {boolean}): {Shader}
UML: {+} constructor(vertexShaderPath: {String}, fragmentShaderPath: {String}): {Shader}
UML: {-} createShader(shaderSource: {String}, shaderType: {ShaderTypes}): {int}
UML: {+} bind(modelMatrixSupplier: {Supplier(Matrix4f)}, material: Material): {void}
UML: {+} unbind(): {void}
UML: {+} destroy(): {void}
UML: {+} getUniformLocation(name: {String}): {int}
UML: {+} getVertexShaderPath(): {String}
UML: {+} getFragmentShaderPath(): {String}

A shader osztály felelős azért, hogy a játékfolyamat során a játék által használt shadereket tárolja.

### Shader típusok
> ShaderTypes

UML: {enum} ShaderTypes
UML: Values:
UML: {VERTEX_SHADER}
UML: {FRAGMENT_SHADER}
UML: {GEOMETRY_SHADER}

### Jegyzőkönyv osztály
> Logger class

UML: {class} Logger
UML: Fields:
UML: {-} log: ArrayList(String) ({static})
UML: Methods:
UML: {-} getTimestamp(): {String} ({static})
UML: {+} log(message: {String}): {void} ({static})
UML: {+} warn(message: {String}): {void} ({static})
UML: {+} error(message: {String}): {void} ({static})

A jegyzőkönyv osztály felelős azért, hogy a játékfolyamat során a játék által generált üzeneteket (információ, figyelmeztetés, hiba) tárolja.

### Entitás gyár osztály
> EntityFactory class

UML: {class} EntityFactory
UML: Fields:
UML: {-} entity: {Entity}
UML: Methods:
UML: {-} constructor(): {EntityFactory}
UML: {+} create(name: {String}, persistent: {boolean}): {Entity}
UML: {+} create(name: {String}): {Entity}
UML: {+} addComponent(component: {Component}): {EntityFactory}
UML: {+} deserialize(data: {String}): {Entity}
UML: {+} buildAndRegister(): {void}

Az entitás gyár osztály felelős azért, hogy az entitásokat létrehozza, és a megfelelő komponensekkel lássa el. A factory elrendezés minden beállítás és komponens létrehozás opcionális és sorrendtől független. Ezelől kivétel az olyan komponensek hozzáadása, amelyeknek a létrehozáshoz szükséges egy másik komponens is (pl. a `RendererComponent`-nek a `TransformComponent`-re van szüksége).

### Entitás
> Entity

UML: {class} Entity : {Serializable}
UML: Fields:
UML: {-} name: {String}
UML: {-} components: {ArrayList(Component)}
UML: {-} hashId: {String}
UML: Methods:
UML: {~} constructor(name: {String}, persistent: {boolean}): {Entity}
UML: {~} constructor(name: {String}): {Entity}
UML: {+} serialize(): {String} ({override})
UML: {+} deserialize(data: {String}): {void} ({override})
UML: {+} addComponent(component: {Component}): {void}
UML: {+} getName(): {String}
UML: {+} removeEntity(entity: {Entity}): {void} ({static})
UML: {+} getComponents(): {ArrayList(Component)}
UML: {+} getComponent(componentClass: {Class(? extends Component)}): {Component}
UML: {+} getHashId(): {String}

Az entitások a játékban megjelenő objektumokat reprezentálják. Az entitásokat komponensek alkotják, amelyek a különböző tulajdonságokat (pozíció, forgatás, skálázás, modell, szerep, stb.) reprezentálják.

### Komponens
> Component

UML: {abstract} {class} Component : {Serializable}
UML: Methods:
UML: {+} getDependencies(): {Class(? extends Component)[]}
UML: {+} fulfillDependencies(entity: {Entity}): {void}

A komponensek a játékban megjelenő objektumok tulajdonságait reprezentálják. A komponensek egy entitáshoz tartoznak, és az entitásokat alkotják. A komponensek lehetnek például pozíciót, forgatást, skálázást, modellt, szerepet, stb. reprezentáló osztályok. Ez az osztály egy absztrakt osztály, amelynek a leszármazott osztályai alkotják a különböző komponenseket.

### Transzformációs komponens
> TransformComponent

UML: {class} TransformComponent : {Component}
UML: Fields:
UML: {-} parentEntity: {Entity}
UML: {-} parent: {TransformComponent}
UML: {-} position: {Vector3f}
UML: {-} rotation: {Vector3f}
UML: {-} scale: {Vector3f}
UML: Methods:
UML: {+} constructor(position: {Vector3f}, rotation: {Vector3f}, scale: {Vector3f}, parent: {TransformComponent}): {TransformComponent}
UML: {+} constructor(position: {Vector3f}, rotation: {Vector3f}, scale: {Vector3f}): {TransformComponent}
UML: {+} constructor(): {TransformComponent}
UML: {+} serialize(): {String} ({override})
UML: {+} deserialize(data: {String}): {void} ({override})
UML: {+} getParent(): {TransformComponent}
UML: {+} getParentEntity(): {Entity}
UML: {+} setParent(entity: {Entity}): {void}
UML: {+} getPosition(): {Vector3f}
UML: {+} setPosition(position: {Vector3f}): {void}
UML: {+} getRotation(): {Vector3f}
UML: {+} setRotation(rotation: {Vector3f}): {void}
UML: {+} getScale(): {Vector3f}
UML: {+} setScale(scale: {Vector3f}): {void}
UML: {+} getWorldPosition(): {Vector3f}
UML: {+} getModelMatrix(): {Matrix4f}

A transzformációs komponens felelős azért, hogy az entitások pozícióját, forgatását és skálázását reprezentálja.

### Kamera komponens
> CameraComponent

UML: {class} CameraComponent : {Component}
UML: Fields:
UML: {-} fieldOfView: {float}
UML: {-} nearPlane: {float}
UML: {-} farPlane: {float}
UML: {-} projectionMatrix: {Matrix4f}
UML: {-} transformComponent: {TransformComponent}
UML: {-} activeCamera: {CameraComponent} ({static})
UML: Methods:
UML: {+} constructor(fieldOfView: {float}, nearPlane: {float}, farPlane: {float}, active: {boolean}): {CameraComponent}
UML: {+} constructor(fieldOfView: {float}, nearPlane: {float}, farPlane: {float}): {CameraComponent}
UML: {+} constructor(): {CameraComponent}
UML: {+} recalculateProjectionMatrix(aspectRatio: {float}): {void}
UML: {+} getActiveCamera(): {CameraComponent} ({static})
UML: {+} setActive(): {void}
UML: {+} getProjectionMatrix(): {Matrix4f}
UML: {+} getViewMatrix(): {Matrix4f}
UML: {+} serialize(): {String} ({override})
UML: {+} deserialize(data: {String}): {void} ({override})
UML: {+} getDependencies(): {Class(? extends Component)[]} ({override})
UML: {+} fulfillDependencies(entity: {Entity}): {void} ({override})
UML: {+} getTransformComponent(): {TransformComponent}
UML: {+} getFieldOfView(): {float}
UML: {+} setFieldOfView(fieldOfView: {float}): {void}
UML: {+} getNearPlane(): {float}
UML: {+} setNearPlane(nearPlane: {float}): {void}
UML: {+} getFarPlane(): {float}
UML: {+} setFarPlane(farPlane: {float}): {void}

A kamera komponens felelős azért, hogy a rajzoláshoz használt nézőpontot reprezentálja.

### Fény komponens
> LightComponent

UML: {class} LightComponent : {Component}
UML: Fields:
UML: {-} lightType: {LightTypes}
UML: {-} color: {Vector3f}
UML: {-} intensity: {float}
UML: {-} transformComponent: {TransformComponent}
UML: Methods:
UML: {+} constructor(lightType: {LightTypes}, color: {Vector3f}, intensity: {float}): {LightComponent}
UML: {+} constructor(): {LightComponent}
UML: {+} serialize(): {String} ({override})
UML: {+} deserialize(data: {String}): {void} ({override})
UML: {+} getDependencies(): {Class(? extends Component)[]} ({override})
UML: {+} fulfillDependencies(entity: {Entity}): {void} ({override})
UML: {+} getLightType(): {LightTypes}
UML: {+} setLightType(lightType: {LightTypes}): {void}
UML: {+} getColor(): {Vector3f}
UML: {+} setColor(color: {Vector3f}): {void}
UML: {+} getIntensity(): {float}
UML: {+} setIntensity(intensity: {float}): {void}
UML: {+} getWorldPosition(): {Vector3f}

A fény komponens felelős azért, hogy a játékban megjelenő fényeket reprezentálja.

### Fény típusok
> LightTypes

UML: {enum} LightTypes
UML: Values:
UML: {POINT}
UML: {DIRECTIONAL}
UML: {SPOT}

### Statikus modell renderelő komponens
> StaticModelRendererComponent

UML: {class} StaticModelRendererComponent : {Component}
UML: Fields: 
UML: {-} mesh: {Mesh}
UML: {-} material: {Material}
UML: {-} shader: {Shader}
UML: {-} transformComponent: {TransformComponent}
UML: {-} meshPath: {String}
UML: {-} diffuseTexturePath: {String}
UML: {-} vertexShaderPath: {String}
UML: {-} fragmentShaderPath: {String}
UML: Methods:
UML: {+} constructor(mesh: {Mesh}, material: {Material}, shader: {Shader}): {StaticModelRendererComponent}
UML: {+} constructor(data: {StaticModelData}, shader: {Shader}): {StaticModelRendererComponent}
UML: {+} constructor(): {StaticModelRendererComponent}
UML: {+} serialize(): {String} ({override})
UML: {+} deserialize(data: {String}): {void} ({override})
UML: {+} getDependencies(): {Class(? extends Component)[]} ({override})
UML: {+} fulfillDependencies(entity: {Entity}): {void} ({override})
UML: {+} getMesh(): {Mesh}
UML: {+} bind(): {void}
UML: {+} setLights(lightsCache: {List(Entity)}): {void}

A statikus modell renderelő komponens felelős azért, hogy a játékban megjelenő statikus modelleket reprezentálja.

### Entitás menedzser osztály
> EntityManager class

UML: {class} EntityManager ({singleton})
UML: Fields:
UML: {-} entities: {ArrayList(Entity)}
UML: {-} entitiesByComponent: {HashMap(String, ArrayList(Entity))}
UML: {-} INSTANCE: {EntityManager} ({static})
UML: {-} dirty: {boolean}
UML: Methods:
UML: {-} constructor(): {EntityManager}
UML: {+} getInstance(): {EntityManager} ({static})
UML: {+} registerEntity(entity: {Entity}): {void}
UML: {#} unregisterEntity(entity: {Entity}): {void}
UML: {#} registerEntityByComponent(entity: {Entity}, componentType: {String}): {void}
UML: {+} reset(): {void}
UML: {+} getEntitiesByName(name: {String}): {Entity[]}
UML: {+} getEntitiesByKeyword(keyword: {String}): {Entity[]}
UML: {+} getEntitiesByComponent(componentType: {String}): {Entity[]}
UML: {+} getEntityByHashId(hashId: {String}): {Entity}
UML: {+} isDirty(): {boolean}
UML: {+} setDirty(dirty: {boolean}): {void}

Az entitás menedzser osztály felelős az entitások élettartamának és perszisztenciájának kezeléséért.
A menedszer egyik legfontosabb szerepe hogy elősegíti megfelelő cachelési stratégiák alkalmazásával a hatákony keresést az entitások között.

### Mentés menedzser osztály
> SaveManager class

UML: {class} SaveManager ({singleton})
UML: Fields:
UML: {-} INSTANCE: {SaveManager} ({static})
UML: {-} saveData: {LinkedList(Serializable)}
UML: Methods:
UML: {-} constructor(): {SaveManager}
UML: {+} getInstance(): {SaveManager} ({static})
UML: {+} registerSaveData(data: {Serializable}): {void}
UML: {+} unregisterSaveData(data: {Serializable}): {void}
UML: {+} serializeFile(): {String}
UML: {-} deserializeFile(json: {String}): {void}
UML: {+} reset(): {void}
UML: {+} generateHashId(): {String}

A mentés menedzser osztály felelős azért, hogy a játékfolyamat során a játék állapotát elmentse, és a játék indításakor a mentett állapotot betöltse. Ez áll a megfelelő kollekciók alkalmazásából, a perszisztens azaz maradandó entitások és komponensek tárolásából, és a megfelelő sorosítási stratégiák alkalmazásából.

### Sorosítható absztrakt osztály
> Serializable abstract class

UML: {abstract} {class} Serializable
UML: Fields:
UML: {-} type: {String} ({final})
UML: Methods:
UML: {+} constructor(): {Serializable}
UML: {+} serialize(): {String}
UML: {+} deserialize(data: {String}): {void}
UML: {+} getType(): {String}

A sorosítható absztrakt osztály felelős azért, hogy az ebből szármozó osztályokat sorosíthatóvá (menthetővé/betölthetővé) tegye. Ezt megfelelő absztrakt utasításokkal teszi lehetővé, amelyeket az ebből származó osztályoknak implementálniuk kell.

### Extra adat menedzser osztály
> ExtraDataManager class

UML: {class} ExtraDataManager : {Serializable} ({singleton})
UML: Fields:
UML: {-} INSTANCE: {ExtraDataManager} ({static})
UML: {-} dataCollection: {HashMap(String, Data(?))}
UML: Methods:
UML: {-} constructor(): {ExtraDataManager}
UML: {+} getInstance(): {ExtraDataManager} ({static})
UML: {+} registerData(key: {String}, data: {Data(?)}): {void}
UML: {+} unregisterData(key: {String}): {void}
UML: {+} getData(key: {String}): {Data(?)}
UML: {+} reset(): {void}
UML: {+} serialize(): {String} ({override})
UML: {+} deserialize(data: {String}): {void} ({override})

Az extra adat menedzser osztály felelős azért, hogy a játékfolyamat során a játék által használt extra adatokat tárolja.

### Extra adat absztrakt osztály
> Data abstract class

UML: {abstract} {generic} {class} Data(T) : {Serializable}
UML: Fields:
UML: {-} data: {T}
UML: Methods:
UML: {+} constructor(data: {T}): {Data(T)}
UML: {+} constructor(): {Data(T)}
UML: {+} getData(): {T}
UML: {+} setData(data: {T}): {void}

Az extra adat absztrakt osztály felelős azért, hogy a játékfolyamat során a játék által használt extra adatokat tárolja. Ez az osztály egy absztrakt osztály, amelynek a leszármazott osztályai alkotják a különböző extra adatokat.
Alapból implementálva pár primitív típus megtalálható.

### Logika menedzser osztály
> LogicManager class

UML: {class} LogicManager ({singleton})
UML: Fields:
UML: {-} INSTANCE: {LogicManager} ({static})
UML: {-} logics: {ArrayList(Logic)}
UML: Methods:
UML: {-} constructor(): {LogicManager}
UML: {+} getInstance(): {LogicManager} ({static})
UML: {+} registerLogic(logic: {Logic}): {void}
UML: {+} unregisterLogic(logic: {Logic}): {void}
UML: {+} reset(): {void}
UML: {+} update(fixedDeltaTime: {float}): {void}
UML: {+} render(deltaTime: {float}): {void}
UML: {+} isLogicPresent(classQuery: {Class(? extends Logic)}): {boolean}

A logika menedszer osztály az ECS rendszer által alkalmazott logikai rendszerek felügyeletéért, futtatásáért és alkalmazásáért felelős osztály. A logikai rendszerek a játékfolyamat során a játéklogika megvalósításáért felelősek.

### Logika absztrakt osztály
> Logic abstract class

UML: {abstract} {class} Logic
UML: Fields:
UML: {-} query: {Query}
UML: {-} enabled: {boolean}
UML: Methods:
UML: {+} constructor(query: {Query}): {Logic}
UML: {+} CallUpdate(fixedDeltaTime: {float}): {void}
UML: {+} CallRender(deltaTime: {float}): {void}
UML: {+} update(queryTargets: {Entity[]}, fixedDeltaTime: {float}, time: {float}): {void}
UML: {+} render(queryTargets: {Entity[]}, deltaTime: {float}, time: {float}): {void}
UML: {+} setEnabled(enabled: {boolean}): {void}
UML: {+} isEnabled(): {boolean}

A logika absztrakt osztály felelős azért, hogy a játékfolyamat során a játéklogikát megvalósítsa. A logikai rendszerek ebből az osztályból származnak, és a megfelelő absztrakt metódusokat implementálják.

### Lekérdezés osztály
> Query class

UML: {class} Query
UML: Fields:
UML: {-} type: {QueryType}
UML: {-} name: {String}
UML: {-} componentTypes: {String[]}
UML: {-} queryCache: {Entity[]}
UML: Methods:
UML: {+} constructor(name: {String}, keyword: {boolean}): {Query}
UML: {+} constructor(name: {String}): {Query}
UML: {+} constructor(components: {Class(? extends Component)[]}): {Query}
UML: {+} execute(): {Entity[]}
UML: {-} queryByKeyword(): {Entity[]}
UML: {-} queryByName(): {Entity[]}
UML: {-} queryByComponents(): {Entity[]}

A lekérdezés osztály felelős azért, hogy a játékfolyamat során a játék által használt entitásokat lekérdezze. A lekérdezés típusa lehet név, kulcsszó, vagy komponens.

### Lekérdezés típusok
> QueryType

UML: {enum} QueryType
UML: Values:
UML: {NAME}
UML: {KEYWORD}
UML: {COMPONENT}

### A jövőbeli tervek
> Future plans

Mint a dokumentum elején is olvasható volt, ez a rengeteg mennyiségű dokumentáció mindössze a motor alapjait tartalmazza a jelenlegi legfrissebb prototípusból. A jövőben a tervek szerint a motor további fejlesztéseket fog kapni, amelyek a következők:


- A jelenlegi prototípusban a motor csak statikus modelleket tud megjeleníteni, a jövőben a tervek szerint a motor képes lesz animált modellek megjelenítésére is.
- A jelenlegi prototípusban a motor csak egyféle fénytípust tud megjeleníteni, a jövőben a tervek szerint a motor képes lesz többféle fénytípus megjelenítésére is.
- Jelenleg nincs fizikai motor a játékban, a jövőben a tervek szerint a motor képes lesz fizikai motorok használatára is.
- Nincs kezelőfelület a játékban, a jövőben a tervek szerint a motor képes lesz kezelőfelület megjelenítésére is.
- stb.