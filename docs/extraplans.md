# Bevezetés
> Introduction

A KRTN-Brigád játékmotorja egy közepes komplexitású, Java nyelven készülő, LWJGL-t alkalmazó, Entity-Component System-re alapuló játékmotor. Ez a dokumentum azért felelős, hogy a megfelelő
diagramokkal szemléltesse az egyes részelemek funkcionalitásait.

# Use-Case diagramok
> Use-Case diagrams

## Nyersanyag betöltés
> Resource loading

## Játéklogika hozzáadása
> Adding game logic

# Osztálydiagram Pseudó-bővítése
> Pseudo-extension of class diagram

Az osztálydiagramunk, ahogy az előző dokumentumban is írtuk egy akkori legfrissebb prototípus ábrázolása volt. Ez természetes nem tartalmazott mindent ahhoz, hogy egy teljeskörű multi-purpose játékmotort lehessen belőle építeni. A következőkben ezeket a hiányosságokat fogjuk pótolni. Az ábrához megfelelő tervezés hiányában nem fogjuk hozzáadni, de szöveges formában leírjuk a hiányzó részeket.

## Bemenetkezelés
> Input handling

UML: {class} InputManager ({singleton})
UML: Fields:
UML: {-} INSTANCE: {InputManager}
UML: {-} keyMap: Map({KeyCode}, {Boolean})
UML: {-} mouseMap: Map({MouseButton}, {Boolean})
UML: {-} mousePosition: {Vector2f}
UML: {-} mouseDelta: {Vector2f}
UML: {-} mouseWheel: {float}
UML: {-} mouseWheelDelta: {float}
UML: {-} keyCallbacks: Map({KeyInputEventDescriptor}, List(Consumer({KeyInputEvent})))
UML: {-} mouseCallbacks: Map({MouseInputEventDescriptor}, List(Consumer({MouseInputEvent})))
UML: Methods:
UML: {+} getInstance(): {InputManager}
UML: {+} isKeyDown({KeyCode}): {boolean}
UML: {+} isKeyUp({KeyCode}): {boolean}
UML: {+} isKeyPressed({KeyCode}): {boolean}
UML: {+} isKeyReleased({KeyCode}): {boolean}
UML: {+} isMouseButtonDown({MouseButton}): {boolean}
UML: {+} isMouseButtonUp({MouseButton}): {boolean}
UML: {+} isMouseButtonPressed({MouseButton}): {boolean}
UML: {+} isMouseButtonReleased({MouseButton}): {boolean}
UML: {+} getMousePosition(): {Vector2f}
UML: {+} getMouseDelta(): {Vector2f}
UML: {+} getMouseWheel(): {float}
UML: {+} getMouseWheelDelta(): {float}
UML: {+} addKeyCallback({KeyInputEventDescriptor}, Consumer({KeyInputEvent})): {void}
UML: {+} addMouseCallback({MouseInputEventDescriptor}, Consumer({MouseInputEvent})): {void}
UML: {+} removeKeyCallback({KeyInputEventDescriptor}, Consumer({KeyInputEvent})): {void}
UML: {+} removeMouseCallback({MouseInputEventDescriptor}, Consumer({MouseInputEvent})): {void}

A játékmotorunknak szüksége van egy osztályra, amelyben a bemeneteket vagyunk képesek kezelni. Ehhez kettő féle módszer is rendelkezésre fog állni.

### Visszahívásos módszer
> Callback method

A visszahívásos módszer lényege, hogy a felhasználó regisztrálja a kívánt eseményeket, és a megfelelő esemény bekövetkeztekor a rendszer meghívja a regisztrált függvényeket. Ez a módszer a leggyorsabb, de a legkevésbé rugalmas is. A felhasználó nem tudja befolyásolni, hogy a rendszer milyen sorrendben hívja meg a függvényeket, és hogy milyen paraméterekkel. A felhasználó csak a regisztrációkor adhat meg paramétereket, amely változtat a rendszer által meghívott függvények tulajdonságain.

### Kérdező módszer
> Polling method

A kérdező módszer lényege, hogy a felhasználó kérdezheti le a rendszertől, hogy milyen események történtek. Ez a módszer hívásintenzív a motor számára, viszont a felhasználó számára a legkényelmesebb. A felhasználó a lekérdezéskor megadhatja a kívánt paramétereket, amely változtat a rendszer által visszaadott értékek tulajdonságain.

Ezen módszer használata esetén a bemenetek állapota minden képkocka előtt frissítésre kerül, így a felhasználó az olyan eseményeket is képes észrevenni, amelyek mindössze egy képkockáig tartanak. Ilyen például a billentyű lenyomása pillanatában történő (isKeyPressed) és felengedése pillanatában történő (isKeyReleased) események.

### Egyéb osztályok elképzelhető terve
> Possible design of other classes

UML: {class} KeyInputEventDescriptor
UML: Fields:
UML: {-} keyCode: {KeyCode}
UML: {-} keyState: {KeyState}
UML: {-} modifiers: {Set(KeyModifier)}
UML: Methods:
UML: {+} constructor({KeyCode}, {KeyState}, {Set(KeyModifier)})
UML: {+} getKeyCode(): {KeyCode}
UML: {+} getKeyState(): {KeyState}
UML: {+} getModifiers(): {Set(KeyModifier)}

UML: {class} MouseInputEventDescriptor
UML: Fields:
UML: {-} mouseButton: {MouseButton}
UML: {-} mouseButtonState: {MouseButtonState}
UML: {-} modifiers: {Set(KeyModifier)}
UML: Methods:
UML: {+} constructor({MouseButton}, {MouseButtonState}, {Set(KeyModifier)})
UML: {+} getMouseButton(): {MouseButton}
UML: {+} getMouseButtonState(): {MouseButtonState}
UML: {+} getModifiers(): {Set(KeyModifier)}

UML: {class} KeyInputEvent
UML: Fields:
UML: {-} descriptor: {KeyInputEventDescriptor}
UML: {-} timestamp: {long}
UML: Methods:
UML: {+} constructor({KeyInputEventDescriptor}, {long})
UML: {+} getDescriptor(): {KeyInputEventDescriptor}
UML: {+} getTimestamp(): {long}

UML: {class} MouseInputEvent
UML: Fields:
UML: {-} descriptor: {MouseInputEventDescriptor}
UML: {-} timestamp: {long}
UML: Methods:
UML: {+} constructor({MouseInputEventDescriptor}, {long})
UML: {+} getDescriptor(): {MouseInputEventDescriptor}
UML: {+} getTimestamp(): {long}

UML: {enum} KeyCode
UML: Fields:
UML: {+} A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V, W, X, Y, Z
UML: {+} NUM_0, NUM_1, NUM_2, NUM_3, NUM_4, NUM_5, NUM_6, NUM_7, NUM_8, NUM_9
UML: {+} F1, F2, F3, F4, F5, F6, F7, F8, F9, F10, F11, F12
UML: {+} ESCAPE, ENTER, SPACE, BACKSPACE, TAB, CAPS_LOCK, SHIFT, CONTROL, ALT, META, MENU, CONTEXT_MENU, SCROLL_LOCK, PAUSE, PAGE_UP, PAGE_DOWN, END, HOME, INSERT, DELETE, ARROW_UP, ARROW_DOWN, ARROW_LEFT, ARROW_RIGHT
UML: etc... (all keys)

UML: {enum} KeyState
UML: Fields:
UML: {+} DOWN, UP, PRESSED, RELEASED

UML: {enum} MouseButton
UML: Fields:
UML: {+} LEFT, RIGHT, MIDDLE, BUTTON_4, BUTTON_5, BUTTON_6, BUTTON_7, BUTTON_8

UML: {enum} MouseButtonState
UML: Fields:
UML: {+} DOWN, UP, PRESSED, RELEASED

UML: {enum} KeyModifier
UML: Fields:
UML: {+} SHIFT, CONTROL, ALT, META

## Felhasználói felület
> User interface

A felhasználói felület is két módon lesz elérhető. Az egyik az Immediate módszer, a másik a Retained módszer. Az Immediate módszer lényege, hogy minden képkocka előtt újra kell rajzolni a felhasználói felületet. A Retained módszer lényege, hogy a felhasználói felületet csak akkor kell újra rajzolni, ha az megváltozott.

### Immediate módszer
> Immediate method

Az Immediate módszer lényege, hogy egy gyorsan felépíthető és rugalmas rendszert ad a UI elkészítésére, viszont teljesítményben és általában kinézetében is hátrányosabban néz ki mint a Retained módszer. A felhasználói felületet minden képkocka előtt újra kell rajzolni, így a felhasználói felületet felépítő elemeknek is minden képkocka előtt újra kell számolniuk a pozíciójukat, méretüket, stb.

UML: {class} ImGuiLayer ({singleton})
UML: Fields:
UML: {-} INSTANCE: {ImGuiLayer}
UML: {-} renderables: List({Runnable})
UML: Methods:
UML: {-} constructor()
UML: {+} getInstance(): {ImGuiLayer}
UML: {+} render(): {void}
UML: {+} registerRenderable(Runnable): {void}
UML: {+} unregisterRenderable(Runnable): {void}

Ez az osztály felelős a felhasználói felület IM részének megjelenítéséért. Ehhez az osztályhoz a motor Logic osztályaiból kell hozzáférni, a konstruktorában kell regisztrálni a felhasználói felületet felépítő utasítását a Logika osztálynak, majd a Logika onUnregister metódusában le kell iratkozni a felhasználói felületet felépítő utasításról.

Az ImGui layer egy már a szakmában gyakran használt és jól elismert DearImGui könyvtár segítségével kerül megvalósításra. Ez azt jelenti hogy a UI használatához szükséges osztályokat ez a dokumentum nem érinti, mivel azok a DearImGui könyvtár részét képezik.
[DearImGui](https://github.com/ocornut/imgui/wiki)

### Retained módszer
> Retained method

A Retained módszer lényege, hogy a felhasználói felületet csak akkor kell újra rajzolni, ha az megváltozott. Ez a módszer teljesítményben és kinézetben is jobb, mint az Immediate módszer, viszont a felhasználói felület felépítése bonyolultabb és kevésbé rugalmas.

UML: {class} UIElement
UML: Fields:
UML: {-} parent: {UIElement}
UML: {-} children: List({UIElement})
UML: {-} position: {Vector2f}
UML: {-} size: {Vector2f}
UML: {-} visible: {boolean}
UML: {-} enabled: {boolean}
UML: {-} focused: {boolean}
UML: {-} hovered: {boolean}
UML: {-} pressed: {boolean}
UML: Methods:
UML: {+} constructor()
UML: {+} getParent(): {UIElement}
UML: {+} getChildren(): List({UIElement})
UML: {+} getPosition(): {Vector2f}
UML: {+} getSize(): {Vector2f}
UML: {+} isVisible(): {boolean}
UML: {+} isEnabled(): {boolean}
UML: {+} isFocused(): {boolean}
UML: {+} isHovered(): {boolean}
UML: {+} isPressed(): {boolean}
UML: {+} setParent({UIElement}): {void}
UML: {+} setPosition({Vector2f}): {void}
UML: {+} setSize({Vector2f}): {void}
UML: {+} setVisible({boolean}): {void}
UML: {+} setEnabled({boolean}): {void}
UML: {+} setFocused({boolean}): {void}
UML: {+} setHovered({boolean}): {void}
UML: {+} setPressed({boolean}): {void}
UML: {+} addChild({UIElement}): {void}
UML: {+} removeChild({UIElement}): {void}
UML: {+} render(): {void}
UML: {+} registerInputs(): {void}

> Kevésbé kifejtett osztályok
UML: {class} UIContainer extends UIElement

UML: {class} UIComponent extends UIElement

UML: {class} UIButton extends UIComponent

UML: {class} UILabel extends UIComponent

UML: {class} UITextBox extends UIComponent

UML: {class} UIImage extends UIComponent

UML: {class} UIProgressBar extends UIComponent

UML: {class} UISlider extends UIComponent

>etc...

Ezeket az osztályokat is egy Logika osztály során kell regisztrálni, és a Logika onUnregister metódusában le kell iratkozni a felhasználói felületet felépítő utasításról.
Viszont ahhoz hogy megfelelő módon legyenek használva, megfelelő callbackekkel kell felszerelni a logika osztályokat.

UML: {class} UIManager ({singleton})
UML: Fields:
UML: {-} INSTANCE: {UIManager}
UML: {-} root: {UIElement}
UML: Methods:
UML: {-} constructor()
UML: {+} getInstance(): {UIManager}
UML: {+} getRoot(): {UIElement}
UML: {+} setRoot({UIElement}): {void}
UML: {+} render(): {void}

Ez az osztály felelős a felhasználói felület RM részének megjelenítéséért.

## Fizika
> Physics

A fizikai megoldás nagyon még nincs konkretizálva a motorban, de az alábbi felépítés elképzelhető.

UML: {class} PhysicsManager ({singleton})
UML: Fields:
UML: {-} INSTANCE: {PhysicsManager}
UML: {-} collisionShapes: List({CollisionShape})
UML: {-} rigidBodies: List({RigidBody})
UML: Methods:
UML: {-} constructor()
UML: {+} getInstance(): {PhysicsManager}
UML: {+} getCollisionShapes(): List({CollisionShape})
UML: {+} getRigidBodies(): List({RigidBody})
UML: {+} addCollisionShape({CollisionShape}): {void}
UML: {+} removeCollisionShape({CollisionShape}): {void}
UML: {+} addRigidBody({RigidBody}): {void}
UML: {+} removeRigidBody({RigidBody}): {void}
UML: {+} update(): {void}

UML: {class} CollisionShapeComponent extends {Component}
UML: Fields:
UML: {-} collisionShape: {CollisionShape}
UML: {-} transform: {TransformComponent}
UML: Methods:
UML: {+} constructor({CollisionShape})
UML: {+} getCollisionShape(): {CollisionShape}

A pozíció és a forgatás úgy kerül tárolásra, hogy az Entitás amelyet ellátunk ezzel a komponensel, már rendelkeznie kell egy TransformComponent-el, amelyben a pozíció és a forgatás is tárolva van.

UML: {class} CollisionShape
UML: Fields:
UML: {-} transform: {Transform}
UML: Methods:
UML: {+} constructor({Transform})
UML: {+} getTransform(): {Transform}

Lehetséges CollisionShape-ek:
UML: {class} BoxCollisionShape extends {CollisionShape}

UML: {class} SphereCollisionShape extends {CollisionShape}

UML: {class} CapsuleCollisionShape extends {CollisionShape}

UML: {class} CylinderCollisionShape extends {CollisionShape}

UML: {class} MeshHullCollisionShape extends {CollisionShape}

Mivel Verlet integrációt fogunk használni, így a sebességet nem kell tárolni, mivel az a pozíció és a forgatás változásából számolható ki.

UML: {class} RigidBodyComponent extends {Component}
UML: Fields:
UML: {-} force: {Vector3f}
UML: {-} torque: {Vector3f}
UML: {-} mass: {float}
UML: {-} acceleration: {Vector3f}
UML: {-} angularAcceleration: {Vector3f}
UML: {-} collisionShape: {CollisionShape}
UML: {-} transform: {TransformComponent}
UML: Methods:
UML: {+} constructor({float})
UML: {+} getForce(): {Vector3f}
UML: {+} getTorque(): {Vector3f}
UML: {+} getMass(): {float}
UML: {+} getAcceleration(): {Vector3f}
UML: {+} getAngularAcceleration(): {Vector3f}

Ez a komponens ismét már meglévő komponenseket használ fel, így a pozíció és a forgatás ismét a TransformComponent-ben lesz tárolva, a CollisionShape pedig a CollisionShapeComponent-ben.

## Hang
> Sound

A hangok lejátszásához a motor OpenAL-t fog használni. 

UML: {class} SoundManager ({singleton})
UML: Fields:
UML: {-} INSTANCE: {SoundManager}
UML: Methods:
UML: {-} constructor()
UML: {+} getInstance(): {SoundManager}
UML: {+} playSound({Sound}): {void}
UML: {+} stopSound({Sound}): {void}
UML: {+} pauseSound({Sound}): {void}
UML: {+} resumeSound({Sound}): {void}
UML: {+} setSoundVolume({Sound}, {float}): {void}
UML: {+} setSoundPitch({Sound}, {float}): {void}
UML: {+} setSoundPosition({Sound}, {Vector3f}): {void}
UML: {+} setSoundLooping({Sound}, {boolean}): {void}
UML: {+} setSoundAttenuation({Sound}, {float}): {void}
UML: {+} setSoundMinDistance({Sound}, {float}): {void}
UML: {+} setSoundMaxDistance({Sound}, {float}): {void}
UML: {+} setSoundRolloffFactor({Sound}, {float}): {void}
etc...

UML: {class} Sound
UML: Fields:
UML: {-} buffer: {SoundBuffer}
UML: {-} source: {SoundSource}
UML: Methods:
UML: {+} constructor({SoundBuffer})
UML: {+} getBuffer(): {SoundBuffer}
UML: {+} getSource(): {SoundSource}

UML: {class} SoundBuffer
UML: Fields:
UML: {-} bufferId: {int}
UML: Methods:
UML: {+} constructor()
UML: {+} getBufferId(): {int}

UML: {class} SoundSource
UML: Fields:
UML: {-} sourceId: {int}
UML: Methods:
UML: {+} constructor()
UML: {+} getSourceId(): {int}

## Optimalizálásra használt osztályok
> Classes used for optimization

UML: {class} Profiler
UML: Fields:
UML: {-} startTime: {long}
UML: {-} endTime: {long}
UML: {-} elapsedTime: {long}
UML: Methods:
UML: {+} constructor()
UML: {+} start(): {void}
UML: {+} stop(): {void}
UML: {+} getElapsedTime(): {long}

UML: {class} OcclusionCullingApplicator
UML: Fields:
UML: {-} camera: {Camera}
UML: {-} frustum: {Frustum}
UML: Methods:
UML: {+} constructor({Camera})
UML: {+} apply({List(Renderable)}): {List(Renderable)}

UML: {class} Frustum

UML: {class} LODApplicator
UML: Fields:
UML: {-} camera: {Camera}
UML: Methods:
UML: {+} constructor({Camera})
UML: {+} apply({List(Renderable)}): {List(Renderable)}

UML: {class} Renderable

etc...

## Szerkesztő
> Editor

A szerkesztő pár opcionálisan hozzáadható osztályból fog állni.

UML: {class} Editor ({singleton})
UML: Fields:
UML: {-} INSTANCE: {Editor}
UML: {-} editorLogic: {EditorLogic}
UML: {-} editorData: {EditorDataHolderComponent}
UML: Methods:
UML: {-} constructor()
UML: {+} getInstance(): {Editor}
UML: {+} getEditorLogic(): {EditorLogic}
UML: {+} getEditorData(): {EditorDataHolderComponent}
UML: {+} unregister(): {void}

UML: {class} EditorLogic
UML: Methods:
UML: {+} constructor() // A Query-t beállítja az Editor a konstruktorban az előre létrehozott adattároló Entity-re
UML: {+} update(): {void}
UML: {+} render(): {void}

UML: {class} EditorDataHolderComponent extends {Component}
UML: Fields:
UML: {-} editorData: {EditorData}
UML: Methods:
UML: {+} constructor()
UML: {+} getEditorData(): {EditorData}

UML: {class} EditorData

A szerkesztő megkönnyíti kollekciók és egyéb adatok kezelését, valamint a játéklogika tesztelését. A játéklogika a JVM-ben található Nashorn JavaScript motorral szkriptelhető módon is bővíthető lesz ennek a segítségével.

# Dinamikus modell
> Dynamic model

![Dynamic model]{scale-img}(./img/dynamic_model.png)

# Alrendszerek
> Subsystems

Az alrendszerekre bontás a következőképpen fog történni:
- Engine (motor)
- Physics (fizika)
- Sound (hang)
- IM (Immediate mode) UI (felhasználói felület)
- RM (Retained mode) UI (felhasználói felület)
- Editor (szerkesztő)

Az alább felsorolt alrendszerek mindegyike egy külön csomagban lesz elhelyezve. Az Engine csomag a fő csomag, amelyben használatra kerülnek a többi csomagban található osztályok.