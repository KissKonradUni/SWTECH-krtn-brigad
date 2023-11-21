# Bevezetés
> Introduction

Mi itt a KTRN brigád csapatával egy multi-purpose 3D-s játékmotort próbálunk készíteni OpenGL és Java használatával.
Ez a dokumentum ennek a projektnek a megfelelő megtervezését alapozza meg.

Egy játékmotor egy olyan szoftver, amelyet a játékfejlesztők használnak, hogy a játékukat készítsék el.
Bár a fogalom eléggé nyilvánvalónak hangzik, a játékmotorok valójában egy nagyon összetett szoftverek.
Rengeteg fejlesztési időt szabadíthat fel a játékfejlesztőknek, ha egy kész játékmotort használnak, mert a motor
már tartalmazza a legtöbb olyan funkciót, amelyekre a játékfejlesztőknek szükségük van. Például a játékmotorok
általában tartalmazzák a fizikai szimulációt, a grafikai megjelenítést, a hangkezelést, a felhasználói felületet,
és még sok más funkciót.

Ezentúl a játékmotorok általában nagyon modulárisak, ami azt jelenti, hogy a játékfejlesztők könnyen tudnak
saját funkciókat hozzáadni a motorhoz, vagy akár a meglévő funkciókat is módosítani tudják.

# Áttekintés
> Overview

A játékmotor kigondolásakor a pár elég fontos szempontot vettünk figyelembe.
Első sorban Java nyelven még nincs sok konkurencia a téren. Ezentúl a konkurencián belül
olyan pedig még annyira se, amely pont azokat a funkciókat tartalmazza amelyeket mi szeretnénk.
Még fontos, hogy tisztán OpenGL-t használunk a 3D-s grafikához. Ez a döntés azért fontos, mert
így a játékmotorunkat bármilyen platformra könnyen át lehet majd portolni.
És végsőként a motornak gyorsnak kell maradnia még a legtöbb eszközön is.

Íme egy Use-Case diagram, amely bemutatja a játékmotorunkat, és a felhasználók által elvégzett műveleteket:
![Use-Case diagram]{scale-img}(./img/Use-Case.png)
(Egy általánosított kifejtés kis- vagy egyéni projektekhez.)


# A rendszer funkciói
> Specific Requirements

A játékmotor rengeteg különböző funckciót kell hogy tartalmazzon, hogy megfeleljen a mai ipari
standardoknak. Ezek közé tartozik a jó minőségű 3D-s grafikai megjelenítés, a fizikai szimuláció,
egy robosztus és könnyen használható felhasználói felület, és még sok más.

A 3D-s megjenítéshez egy Fizikai alapú megjelenítési modellt fogunk használni, amelyet a Physically Based Rendering
rövidítéssel PBR-nek szoktak hívni. A fizikai szimulációhoz pedig agy Verlet-itegrációra alapuló modellt fogunk alkalmazni. A szerkesztéshez egy egyszerű, de mégis robosztus felhasználói felületet fogunk használni, amelyet a Dear ImGui könyvtár segítségével fogunk megvalósítani. A játékban használatos Retained Mode GUI-t pedig saját magunk fogjuk megvalósítani. Az adattárolást, és a játékban található entitásokat pedig egy Entity-Component-System modell alapján fogjuk megvalósítani.

## Fizikai alapú megjelenítés
> Physically Based Rendering

A Physically Based Rendering egy olyan megjelenítési modell, amely próbálja a valósághű megjelenítést
elérni, akár ray vagy path tracing technikákat is alkalmazva. A PBR modell a valóságban a fények
viselkedését próbálja szimulálni. Ehhez szüksége van a pályában elhelyett fényforrásokra, a fények
színére, a fények intenzitására, a modellek anyagának tulajdonságaira, és még sok másra.

### A fények
> Lights

A fényeket a játékban a játékfejlesztőknek kell hogy elhelyezzék a pályán. A fényeknek többféle típusa
is van, amelyek mindegyike más és más módon viselkedik:


- A pont fények azokat a fényforrásokat jelképezik, amelyek egy pontból sugároznak ki fényt minden irányba.
- A reflektor fények azokat a fényforrásokat jelképezik, amelyek egy irányba sugároznak ki fényt, és egy konkrét szögben van egy reflektoruk, amely a fényt egy irányba koncentrálja.
- A direkcionális fények azokat a fényforrásokat jelképezik, amelyek egy irányba sugároznak ki fényt, és azok a fények párhuzamosan haladnak egymással. Ezek a fények a legtöbb esetben a Napot jelképezik.
A fényeknek ezentúl fontos tulajdonsága, hogy a PBR modellben már bevállt módon a távolság szerint az 
intenzitásuk csökken egy quadratikus függvény szerint.

Ezentúl a fényeknek is van színe, amelyet a rajzolás során a megjelenített anyag színével össze kell
szorozni, hogy a megfelelő színt kapjuk. A fények egy inteziással is rendelkeznek, amely azt jelenti,
hogy a fények mennyire erősek, milyen távolságig világítanak.

### A modellek anyagai
> Materials

A modellek anyagai a PBR modellben a valóságban a modellek anyagának tulajdonságait jelképezik.
A motor ezeket a tulajdonságokat a modellek betöltésekor fogja beolvasni az ASSIMP könyvtár segítségével.

#### Diffúz
> Diffuse

A diffúz a modellek alap színe, amelyet a megjelenítés során a fények színeivel össze kell szorozni,
hogy a megfelelő színt kapjuk. Ez gyakorlatilag az a szín amelyet a szétszórt fény vesz fel a modellről 
történő visszaverődéskor.

#### Spekuláris
> Specular

A spekuláris a modellek fényes színe, azaz a nem szétszórt fény színe. Ez a szín a modell felületén
egy fókuszáltabb pontba koncentrálódik.

#### Érdesség
> Roughness

Az érdesség azt jelenti, hogy a modell felülete mennyire sima. Ez a tulajdonság a modell felületén
a szétszórt fény színét befolyásolja. Minél simább egy felület, annál inkább spekuláris lesz, és
annál kevésbé diffúz.

Azzal, hogy ez az érték nem egy szám, hanem egy textúra, a modell felületén extra részleteket lehet
elérni, mint péládul a karcokat, vagy a kopásokat.

#### Fémesség
> Metallic

A fémesség azt jelenti, hogy a modell mennyire fémes. A PBR modellben a fémesség és az érdesség együtt
határozzák meg a modell felületén a szétszórt fény színét.

Azzal, hogy ez az érték nem egy szám, hanem egy textúra, a modell felületén extra részleteket lehet
elérni, mint péládul eltérő anyagok egy modellen belül, vagy rozsda, vagy kopás.

#### Normális
> Normal map

A normális térkép egy olyan kép, amely a modell felületén a normálvektorokat tartalmazza. A normálvektorok
a modell háromszögeiből kifelé mutatnak, viszont ha egy érdes felületet szeretnénk megjeleníteni, akkor
rengeteg háromszögre lenne szükségünk, hogy a megfelelő hatást elérjük. Ezért a normál térképekkel
a modell felületén a normálvektorokat egy képben tároljuk, és a megjelenítés során eltorzítjuk a normálvektorokat,
hogy azok a megfelelő hatást adják.

Ezzel úgy adunk részletet a modellhez, hogy a háromszögek számát nem növeljük meg.

#### Környezeti fedés
> Ambient occlusion

A környezeti fedés egy olyan kép, amely a modell felületén a környezeti fedési értékeket tartalmazza.
A környezeti fedési értékek azt jelzik, hogy a modell egy adott pontján mennyire van elzárva a fény.
Minden olyan pont, amely közel van egy másik felülethez, például egy konkáv rész, vagy egy éles szög,
vagy egy sarok ahol két felület találkozik, azokon a pontokon a fény nehezebben tud eljutni, ezért
azokon a pontokon a környezeti fedési érték magasabb lesz.

Ezzel az árnyékolást befolyásoljuk, hogy a modell mély részei legyenek sötétebbek, és a felületei
világosabbak.

#### Emisszió
> Emission

Az emisszió egy olyan szín, amelyet a modell a megjelenítés során kibocsájt. Ez a szín nem függ a
fények színétől, és az intenzitásától, hanem mindig ugyanaz marad. Ez természetesen nem minden 
anyag esetén van jelen, csak azoknál amelyek képesek fényt kibocsájtani.

Ezt nem kell megszorozni a fények színével, mert ez a szín mindig ugyanaz marad.

### Framebuffer objektumok
> Framebuffer objects

A framebuffer objektumok olyan objektumok, amelyek a megjelenítés során a képet tárolják. Ezzel több fázisra
bontjuk a megjelenítést, és a végeredményt csak a végén jelenítjük meg. A framebuffer objektumoknak többféle
típusa is van. A legfontosabb használati területei az implementálás során a következők:


- `Base rendering pass` - A base rendering pass egy olyan framebuffer lesz, amely a megjelenítés első fázisában lesz használva. Ebben a fázisban a megfelelő shader szerint (amely általában PBR) megjelenítésre kerül a játékban található összes éppen aktív entitás. A háttere ennek átlátszóra lesz hagyva. Ez a réteg egy `color` és egy `depth` csatolmányt fog tartalmazni.
- `Skybox rendering` - A skybox egy úgynevezett `HDRI` kép, amely egy gömbfelületre van vetítve. A skybox azokra a területekre lesz kirajzolva, amelyek az első fő rajzolási lépés során átlátszónak lettek meghagyva. Ennek a technikának köszönhetően a háttér egy geometria nélkül is megoldható.
- `Shadow pass from lights` - A shadow pass egy olyan framebuffer lesz, amely során a felől lesz ferméve a távolság minden entitásra. Ez a réteg egy `depth` csatolmányt fog tartalmazni, és a fények által vetített árnyékokat fogja felhasználni. Ezt felhasználva fogjuk tudni elkészíteni a valós idejű árnyékokat.
- `Shadow pass from camera` - A shadow pass egy olyan framebuffer lesz, amely során a fények árnyékai kerülnek megjelenítésre. Ez a réteg egy `depth` csatolmányt fog tartalmazni, és a fények által vetített árnyékokat fogja felhasználni.
- `Transparent rendering pass` - A transparent rendering pass egy olyan framebuffer lesz, amely (általában) a megjelenítés során az árnyékok után következik be. Ebben a fázisban az átlátszó entitások kerülnek megjelenítésre. Ez a réteg is egy `color` és egy `depth` csatolmányt fog tartalmazni.
- `Post-processing rendering pass` - A post-processing rendering pass egy olyan framebuffer lesz, amelyben a post-processing effektek kerülnek megjelenítésre. Ez a réteg annyi alkalommal lesz használva, ahány post-processing effektet szeretnénk alkalmazni. Ez a réteg is egy `color` és egy `depth` csatolmányt fog tartalmazni.
Egyéb rétegek, amelyek a szerkesztőben lesznek használva:


- `Selectable pass` - A selectable pass egy olyan framebuffer lesz, amely során minden entitás egy külön hash alapján készült színnel fog megjelenni. Ez a réteg egy `color` csatolmányt fog tartalmazni. Ezzel a színnel beazonosítható hogy mely objektumra kattintott a szerkesztőben a felhasználó.
- `Selection mark pass` - A selection mark pass egy olyan framebuffer lesz, amely során a kiválasztott entitásokra egy külön jelölés kerül megjelenítésre. Ez a réteg egy `color` csatolmányt fog tartalmazni.
- `Wireframe pass` - (Opcionálisan állítható) A wireframe pass egy olyan framebuffer lesz, amely során minden entitás a vertexek által alkotott éle kerül megjelenítésre. Ez a réteg egy `color` csatolmányt fog tartalmazni.

### Árnyékok
> Shadows

A játékmotorokban a legtöbb esetben a fények árnyékot is vetnek. Az árnyékokat a motor két féle módon
tudja megjeleníteni. Az egyik módszer a `Percentage Closer Filtering`, amely egy valós időben történő
árnyék megjelenítési módszer. Ez a módszer a legtöbb esetben elég jó eredményt ad, de a legtöbb esetben
nem elég pontos. A másik módszer egy a `Shadow Map`-re alapuló módszer, amely egy előre számolt árnyék
megjelenítési módszer. Ez a módszer sokkal pontosabb, nagyobb felbontású árnyékokat eredményez, de
nem változik valós időben, és a legtöbb esetben nem is lehet változtatni a fények helyzetén.

### Post-processing effektek
> Post-processing effects

A post-processing effektek olyan effektek, amelyeket a megjelenítés után alkalmazunk a képre.
Ezek az effektek nem a PBR modell részei, de a játékmotorokban nagyon gyakran használják őket.
Csak hogy felsoroljunk párat, amelyet tervezünk a motor részévé tenni:


- `Bloom` - A fényes részeket kiszűri, és azokat egy másik képre rajzolja, majd azt elmosva visszailleszti a képre. Ezzel kiemeli a fényforrásokat.
- `Depth of field` - A kép egy részét élesre, a többi részét pedig elmosva rajzolja ki. Ezzel a hatással a kép egy részét lehet kiemelni. Ilyen például egy fegyver irányzékja.
- `Motion blur` - A kép egy részét elmosva rajzolja ki. Ezzel a hatással a mozgás hatását lehet elérni.
- `Tone mapping` - A kép színeit úgy alakítja, hogy azok a monitor színeihez legyenek igazítva. Ezzel a hatással a kép színei valósághűbbek, vagy művésziebbek lehetnek.
- `Vignette` - A kép széleit sötétítve rajzolja ki. Ezzel a hatással a kép középpontját lehet kiemelni.
- `Chromatic aberration` - Eltolja a kép piros és kék csatornáit a z-buffer értékének megfelelően. Ezzel a hatással egy pszhiedelikus hatást lehet elérni. Alkalmazható sebesülés, vagy mérgezés hatásának jelzésére.
- `Lens distortion` - Eltorzítja a képet. Ezzel a hatással egy kamera lencséjének torzulását lehet elérni.
- `Grain` - A kép színeit véletlenszerűen megváltoztatja. Ezzel a hatással egy régi film hatását lehet elérni.
- `Fog` - A kép távolabbi részeit elfedi egy köddel. Ezzel a hatással a távolabbi részeket lehet elrejteni, és a közeli részeket kiemelni.
- `Color grading` - A kép színeit úgy változtatja meg, hogy azok a kívánt színekhez legyenek igazítva. Ezzel a hatással a kép színei valósághűbbek, vagy művésziebbek lehetnek. Nem ugyanaz mint a Tone mapping.
- `Sharpen` - A kép élességét növeli. Ezzel a hatással a kép részletei kiemelhetőek.
- `Dithering` - A színekelmélet terén könnyen észrevehető hogy a sötét színek közti különbség feltűnőbb mint a világos színek közti. Ezért a dithering egy olyan effekt, amely a úgy próbálja keverni a színeket hogy a hatalmas ugrások kevésbé legyenek feltűnőek. Ezzel a hatással a színek átmenetei kevésbé lesznek észrevehetőek.
- `Gamma correction` - A kép színeit úgy változtatja meg, hogy azok a monitor színeihez legyenek igazítva. Ez abból ered, hogy a kép amelyet a játék készít lehet világosabb, mint a maximum fehér amelyre a monitor képes. Ezzel a hatással újrakalibrálhatóak a színek.
- `SSAO` - A kép azon részeit amelyek a környezeti fedés miatt sötétebbek, azokat még sötétebbé teszi. Ezzel a hatással a mélyebb részek kiemelhetőek. Praktikusabb mint a PBR anyagban megadott környezeti fedés, mert ez működik mozgó objektumokon is.
- `SSR` - Veszi a kép azon részeit, amelyet már egyszer kirajzoltunk, és megpróbálja felhasználni a környező objektumok tükröződésének kiszámításához. Ezzel a hatással a tükröződések elégségesek lehetnek.
- `Pixelation` - A kép felbontását csökkenti. Ezzel a hatással a kép művésziebb lehet. 
- `FXAA` - A kép éleit elmosva rajzolja ki. Ezzel a hatással a kép élei kevésbé lesznek észrevehetőek.

## Rajzolási optimalizálási problémák
> Rendering optimization problems

A játékmotorunknak képesnek kell lennie megjeleníteni a kommerz formátumokban elérhető 3D-s modelleket.
Ezt az ASSIMP könyvtár segítségével fogjuk megoldani. Ezek a formátumok viszont nem alkalmasak arra,
hogy a egy kész már fejlesztésen átesett játékot szolgáljunk vele. Több probléma is van velük, amelyet
a motor minden betöltésnél kell hogy eszközöljön.

### A modellek nem "keresztlépéses" formátumban vannak
> The meshes are not in a strided format

A modellek nem olyan formátumban vannak, hogy azokat a videókártya memóriájába hatékonyan lehessen
használni. Ezért a motor minden betöltésnél a modelleket egy stride formátumba kell konvertálja. Ez 
annyit takar, hogy a modell különböző információit amik a csúcsokról szólnak, mint páldául:


- a koordinátái
- a normálvektorai
- a textúra koordinátái
- a csontokhoz tartozó súlyai
csoportokban tárolja a fájl maga, azaz az összes csúcs koordinátája egy tömbben van, az összes normálvektor
egy másik tömbben, és így tovább.

Ez viszont nem elég hatékony nagy mennyiségű adat esetén.
A "keresztlépéses" azaz stride formátum viszont egy olyan formátum, amelyben a csúcsok adatai egy tömbben
vannak csúcsonként csoportosítva, azaz egymás után van minden adat egy csúcsra vonatkozóan.

Ezt a jövőben egy saját formátummal fogjuk megoldani, amelyet a motorunk fog használni.
Mindössze egy rövid konverziós lépést fog igényelni a betöltésnél.

Ahhoz, hogy mindez ne gátolja a modolhatóságot, a nem ebben a formában található fájlokat a motor
első indulásnál konvertálja a stride formátumba, becache-eli, és a jövőben már csak azt fogja használni.
Fájlváltozást észlelni fogunk majd a modell MD5 hash-ének segítségével, amelyet mindig elmentünk a
konvertálás után.

### A modelleket futásidőben láthatósági tesztekkel kell ellenőrizni
> The meshes must be tested for visibility at runtime

A modelleknek nem csak a videókártya memóriájába való hatékony betöltését kell hogy megoldja a motorunknak,
hanem a megjelenítését is. A modelleknek csak akkor kell megjelenniük, ha azok láthatóak a kamera
szemszögéből. 

Ez a számítás viszont nagyon drága, ezért korlátoznunk kell, hogy mire alkalmazzok azokat.
Optimalizálást azokkal az objektumokkal fogunk elvégezni, amelyek statikusak, azaz nem mozognak.
Ezt a betöltés során jelölni lehet az entitásoknál.

### A modelleknek a hátsó oldalát ki lehet hagyni
> The back faces of the meshes can be culled

A modelleknek a hátsó oldalát ki lehet hagyni, mert azokat úgysem látjuk. Ez a számítás viszont 
sokkal gyorsabb, mint a láthatósági teszt, ezért ezt mindig el kell végezni amikor lehet. Egyetlen
kivétel az, amikor a modell átlátszó, mert akkor a hátsó oldalakat is látni fogjuk.
Szerencsére az OpenGL rendelkezik a megfelelő funkcionalitással hogy ezt a számítást elvégezze.

### A túlrajzolás elkerülése
> Avoid overdraw

A túlrajzolás azt jelenti, hogy egy adott pixel többször felülírásra kerül.
Ezzel a problémával a motorunknak is szembe kell néznie, és meg kell próbálnia minimalizálni.
A túlrajzolás elkerülésére több módszert is megpróbálunk majd alkalmazni.

Ilyen például a "frustum culling", amely azt jelenti, hogy a motorunk csak azokat a modelleket fogja
megjeleníteni, amelyek a kamera látószögében vannak. Ez a számítás közepes költséggel jár, de a
túlrajzolás elkerülésével nyert idővel bőven kifizetődik.

A másik fontos techinake a "Z-sorting", amely azt jelenti, hogy a motorunk a modellek megjelenítése
előtt sorba rendezi azokat a távolságuk alapján. Ez a számítás elég drága lenne, de maga az
algoritmus nem kell hogy minden egyes képkockánál lefusson. A modellek csak akkor kell hogy sorba
legyenek rendezve, amikor azok megváltoztak a helyzetüket, vagy amikor újak kerülnek a képernyőre, és
akkor sem fontos hogy egyből reagáljon a motorunk, hanem akár egy kis késleltetéssel is megteheti.
Ezt a megoldást egy "lazy Z-sorting" névvel illetjük. Egyetlen probléval kell szembenéznünk ezzel az
algoritmussal viszont, mégpedig az, hogy az átlátszó objektumok helyes megjelenése végett a motorunknak
csakis azokat egy külön listában kell hogy tárolja, és azokat külön kell hogy megjelenítse. A rendezésnek
ellentétes sorrendben kell hogy történjen, mint a többi objektumnak, hogy a megfelelő eredményt kapjuk.

### A textúrák betöltésének optimalizálása
> Texture loading optimization

A textúrák, azaz képfájlok amelyeket alkalmazunk a modellek megjelenítéséhez sem olyan formában vannak
tárolva, hogy azokat a videókártya memóriájába hatékonyan lehessen betölteni. Ezért a motorunknak minden
betöltésnél ezeket egy nyers, tömörítetlen formába kell hogy rendezze, és azt kell hogy használja.

Ezeket a konvertált képeket majd tervezzük cache-elni, hogy a jövőben ne kelljen újra konvertálni őket.
A kész játék mindössze a becache-elt képeket fogja a felhasználóknak szolgálni egy enyhe direkt veszteségmentes 
tömörítéssel.

## A fizikai szimuláció
> Physics simulation

A fizikai szimuláció a játékmotorok egyik legfontosabb része. A fizikai szimuláció a játékban található
objektumok mozgását szimulálja. A fizikai szimulációhoz többféle modellt is használnak, amelyek közül
mi a Verlet integrációra alapuló modellt fogjuk használni. A Verlet integráció egy olyan modell, amely 
a jelenlegi pozíciót, az előző pozíciót, a gyorsulást, és a tömegét használja fel a következő pozíció
kiszámításához. A Verlet integráció előnye, hogy determinisztikus, azaz minden esetben ugyanazt az eredményt
fogja adni, ha ugyanazokkal az adatokkal számolunk. Ezen kívül a Verlet integráció nagyon egyszerű, és
gyors is, ezért a legtöbb játékmotorban ezt használják.

A determinisztikát egyedül a Floating Point számítások miatt nem tudjuk garantálni.

### A Verlet integráció
> Verlet integration

A Verlet integráció egy olyan modell, amely a jelenlegi pozíciót, az előző pozíciót, a gyorsulást, és a tömegét
használja fel a következő pozíció kiszámításához. 
Ez az algoritmus a következő képlettel számolja ki a következő pozíciót:

$$x_n = 2 \cdot x_n - x_{n-1} + a \cdot \Delta t^2$$
ahol $x_n$ a jelenlegi pozíció, $x_{n-1}$ az előző pozíció, $a$ a gyorsulás, és $\Delta t$ az időlépés.

A tömeg a gyorsulás számításához kell, amelyet a következő képlettel számolunk ki:

$$a = F / m$$
ahol $F$ a rá ható erő, és $m$ a tömeg.

### Ütközések
> Collisions

A fizikai szimulációban az egyik legfontosabb rész a különböző objektumok közötti ütközések szimulálása.
Erre a legtöbb motor különböző ütközési alakzatokat ad rendelkezésre, amelyek az alábbiak lehetnek:


- gömb
- kapszula
- doboz
- a modellről alkotott konvex burkoló alakzat
A legutóbbi alakzattal van a legtöbb probléma, de szükséges a létezésük miután senki nem szeretné kézzel
körbevonni az alagzatokat a primitivekkel.

Az ütközés észrvételére minden estet külön kell kezelni, ami már ezzel a négy alakzattal is $4^2 = 16$ esetet
jelent. 

Arról nem is beszélve, hogy valami alapján el kell dönteni, hogy melyik alakzatot hasonlítsük össze melyikkel a térben,
hiszen bár számunkra ránézésre ez egy egyszerű feladat, de a számítógép számára olyan mintha vakon, süketen és tapintás
nélkül kellene megoldania ezt a feladatot szimplán rengeteg kooridnáta alapján.

Egyenlőre úgy tűnik hogy a legjobb megoldás az ütközés tesztelésére a GJK, azaz a Gilbert-Johnson-Keerthi algoritmus,
lesz amelyet a jövőben fogunk megvalósítani. Ez még változhat.

### Téroptimalizáció
> Spatial optimization

A téroptimalizáció azt jelenti, hogy a motorunk nem fogja az összes objektumot egyszerre vizsgálni, hanem
csak azokat, amelyek elég közel vannak egymáshoz, hogy ütközhetnek. Ezt a számítást egy "térbeli partícionálás"
névvel illetjük. A térbeli partícionálásnak többféle módszere is van, amelyek közül mi egy elég elterjedtet fogunk
használni, a Spatial Hash Grid-et. A Spatial Hash Grid egy olyan adatszerkezet, amely egy rácsot használ a tér
partícionálására. A rács cellái tartalmazzák azokat az objektumokat, amelyek a cella területén belül vannak.
Ez a számítás elég gyors, és egyszerű, de nem tökéletes. A Spatial Hash Grid ugyanis nem tudja kezelni azokat
az objektumokat, amelyek a rács celláinak határain vannak, ezért ezeket az objektumokat minden a határon lévő
cellában tárolni kell. A rendezés, a testek téglatest alakú térfogata alapján történik.
Ezzel az optimalizálással nem kell minden testet minden másikkal összehasonlítani ($n^2$), hanem csak azokat,
amelyek a rácsnak ugynabban, vagy a szomszédos cellákban vannak.

### Többszálúsítás
> Multithreading

A fizikai szimuláció egy olyan része a játékmotoroknak, amelyet nagyon könnyű többszálúsítani. A többszálúsítás
egy olyan technika, amelynek segítségével a számításokat több szálon, egyszerre több processzormagon futtatjuk.

A Java nyelv rendelkezik egy ExecutorService nevű osztállyal, amelyet a többszálúsításhoz fogunk használni.
Ez az osztály egy `ThreadPool`-t fog létrehozni, amelyben a szálak fognak futni. A `ThreadPool`-ban a szálak
egy várakozási sorban fognak várakozni, amíg a szükséges erőforrások felszabadulnak. Ezzel a megoldással
nem kell a szálakat kézzel létrehozni, és kezelni, hanem a Java nyelv fogja ezt megtenni helyettünk.

Ez könnyen használható az előző pontban említett téroptimalizációval, mert a szálakat a rács celláinak
feldolgozására lehet használni. Ezzel a megoldással a számításokat a szálak fogják elvégezni, és a
fő szál csak a szálak eredményeit fogja összegyűjteni.

## A felhasználói felület
> User interface

A felhasználói felület a játékmotorokban kettő fő szerepet szolgálnak. Egyrészt a játékfejlesztőknek
kell hogy egy olyan felületet biztosítsanak, amelyen keresztül a fejlesztés egy gyorsabb és egyszerűbb
folyamat lesz. Ezt a felületet a motor szerkesztőjének nevezzük. Ezen kívül a játékfejlesztőknek kell
egy olyan felületet biztosítaniuk, amelyen keresztül a játékosok tudják irányítani a játékot.

Ahhoz, hogy egy Grafikus Felhasználói Felületet, azaz GUI-t tudjunk megvalósítani több, már jól bevált
módszer is létezik. Ezek közül mi a kettőt is alkalmazni fogunk az eltérő előnyök miatt.

### Az Immediate Mode GUI
> Immediate Mode GUI

Az Immediate Mode GUI egy olyan módszer, amelyben a GUI elemek logikáját és megjelenítését egy függvény
végzi el. Ez a módszer nagyon egyszerű, és gyors, de nagy méreteknél nem hatékony, mert minden egyes
képkockánál újra kell számolni az elemeket. Ez a módszer a legtöbb játékmotorban megtalálható, mert
a szerkesztőkben nagyon jól használható, és a játékokban lehetőséget ad a modolhatóságra.

Ez a módszer lesz az, amelyet a motorunk szerkesztőjében fogunk használni.

### A Retained Mode GUI
> Retained Mode GUI

A Retained Mode GUI egy olyan módszer, amelyben a GUI elemek logikáját és megjelenítését külön függvények
végzik el. Ez a módszer hatékonyabb, mint az Immediate Mode GUI, mert a GUI elemeket csak akkor kell újraszámolni, amikor azok megváltoznak. Enyhe hátránya, hogy ahhoz hogy a logikai részeiben is használjuk a GUI elemeket, először létre kell hozni azokat, külön egy "Binding" fázis részében csatolni visszahívásokat az egyes akciókhoz, mint például egy gomb megnyomása, vagy egy szöveg beírása, és csak utána kezelni a változásokat. Nagyon nagy figyelmet kell szánni ezeknél a kezelőfelületeknél a szinkronizációra, miután az elemek gyakorlatilag függetlenül végzik a dolgukat a háttérben, és a fő szál csak a változásokat fogja észrevenni. 

Ez a módszer az amelyet ösztönözni fogunk a játékfejlesztőknek, hogy a játékaikban használjanak. Ennek ellenére az Immediate Mode GUI-t is elérhetővé fogjuk tenni a kész játékban, hogy a játékfejlesztőknek legyen lehetőségük a modolhatóságra.

## Az adattárolás
> Data storage

A játékmotorokban az adattárolás egy nagyon fontos része a tervezésnek. Az adattárolás a játékban
található entitásokat, azaz objektumokat, és azok tulajdonságait jelenti. Az adattárolásnak többféle
módja is van, amelyek közül mi egy Entity-Component-System modellt fogunk használni. Az Entity-Component-System
modell egy olyan modell, amelyben az entitásokat, azaz objektumokat egy egyedi azonosítóval látjuk el,
és az entitásoknak csak tulajdonságokat, azaz komponenseket adunk.

### Az entitások
> Entities

Az entitások azok az objektumok, amelyeket a játékban találhatunk. Az entitásoknak egy egyedi azonosítóval
kell hogy rendelkezzenek, amelyet a motor fog generálni. Az entitásoknak csak komponenseik lehetnek, amelyek
tartalmazzák az entitások tulajdonságait. Az entitásoknak nincs saját logikájuk, azaz nem tudnak semmit csinálni,
maximum minimális kalkulációt végezhetnek el a komponenseikben.

### A komponensek
> Components

A komponenseknek többféle típusa is van, amelyek közül a beépített típusok a következők a jelenlegi prototípusban:


- `TransformComponent` Transzfomáció, amely tartalmazza az entitás pozícióját, forgatását, és skáláját
- `RendererComponent` Rajzolási, amely tartalmazza az entitás modelljét
- `CameraComponent` Kamera, amely tartalmazza a kamera tulajdonságait
- `LightComponent` Fény, amely tartalmazza a fény tulajdonságait
Ezen kívül a játékfejlesztőknek lehetőségük van saját komponenseket is létrehozni, amelyeket a játék
futása során tudnak hozzáadni az entitásokhoz.

### A rendszerek
> Systems

A rendszerek azok a komponensek, amelyek a játék logikáját tartalmazzák. A rendszereknek a konstruktora
mindig tartalmaz egy `Query` paramétert, amely egy olyan objektum, amely segítségével a rendszer
kiválaszthatja azokat az entitásokat, amelyek az adott logikai részhez szükségesek. 
A kiválasztásnak többféle módja is van, amelyek közül a jelenlegi prototípusban a következők vannak:


- Név alapján
- Kulcsszó alapján
- Komponens alapján
A komponens alapján történő kiválasztás a leggyakrabban használt, mert a játékfejlesztőknek csak a komponenseket
kell hogy ismerjék, és nem kell a nevekkel, és kulcsszavakkal foglalkozniuk.
Ez ugye alapvetően egy eléggé optimalizálatlan megoldásnak hangzik, de egy remek cache-elési lehetőséget
kihasználva gyakorlatilag alig van hatással a teljesítményre. A megoldásunk a problémára az volt, hogy ha az entitáshoz
hozzáadunk egy komponenst, akkor a komponensek szerint vett `HashMap` megfelelő kulcsánál vesszük az ott található `ArrayList`-et, és hozzáadjuk az Entitás referenciáját.

Természetesen a név/kulcsszó alapján történő keresést sem hagyhattuk optimizálatlanul, ezért a név alapján történő keresésnél egy "Dirty" azaz "Koszos" jelzőt használ a tároláshoz használt osztály, amely jelzi, hogy a tárolt adatok
módosultak, és újra kell számolni a keresést. Amennyiben nem módosultak, akkor a keresés nem fog újra történni,
hanem a korábban kiszámolt eredményt fogja visszaadni.

A rendszerek még egy roppant fontos tulajdonsága, hogy futások a hozzáadásuk sorrendjében fognak történni.
Ezentúl a rendszerek is lementésre kerülnek, és a jövőben a motor indulásakor újra betöltődnek.

### A játékfájlok
> Game files

Mostmár felmerülhetettet az a kérdés, hogy mi lehet ez a mágikus módszer, ahogyan egy egész kész rendszer állapotát
egy darab fájlba lementjük és visszatöltjük. A válasz az, hogy valójában ez a fájl egy úgynevezett összeköttető fájl,
amely egy JSON formátumú fájl, amelyben a rendszerek állapotát egy JSON objektumként tároljuk. Amennyiben valami nagyobb
adatméretű objektumot kell tárolni, akkor azt egy külön fájlba tesszük, és a fő fájlban csak a fájl elérési útját
tároljuk. Ilyenek például a modellek, a textúrák, és a hangok. Így a betöltési idő nem szenvedi meg az ember által 
olvasható fájlok hátrányait, és a fájlok mérete is kisebb lesz.

## A játék kódszerkezete
> Game code structure

A játék kódszerkezete a játékmotorokban egy nagyon fontos része a tervezésnek. Amennyiben már az elején nem 
megfelelően tervezzük meg a kódszerkezetet, akkor a jövőben nagyon nehéz lesz bármilyen változtatást végrehajtani.


### Singletonok
> Singletons

A játékmotorokban a Singletonok egy olyan osztályok, amelyekből csak egyetlen egy példány lehet. A Singletonok
ebben az implementációban azért kerültek gyakori használatra, mert praktikusak, és egyszerűek. 

A jelenlegi prototípusban a következő Singletonok vannak:


- `EntityManager` Az entitásokat kezelő osztály
- `LogicManager` A rendszereket kezelő osztály
- `ResourceManager` A fájlok betöltését kezelő osztály
- `SaveManager` A mentést kezelő osztály -> Ez a jövőben több osztályra lesz bontva, miután nem egységes részekben akarunk szigorúan menteni
- `Window` Az ablakot kezelő osztály, a Singletonná tételének oka, hogy a motor szigorúan egy ablakot fog használni, illetve szükség volt arra hogy direkt referencia nélkül bárhol elérhető legyen

### Tick ráta, avagy a logikai frissítések gyakorisága
> Tick rate, a.k.a. the frequency of logic updates

A játékmotorokban a Tick ráta, azaz a logikai frissítések gyakorisága egy olyan érték, amely azt jelenti,
hogy a játék logikája hányszor fog frissülni másodpercenként. Ez az érték eltérő a játék típusától függően.
A régebbi módszerek általában feltételezték a fix képkockarátát, és ahhoz igazították a Tick ráta-t. Ez a
módszer azonban nem elég rugalmas, ezért a jelenlegi prototípusban a Tick ráta független a képkockarátától.
Ezt egy külön szál alkalmazásával oldottuk meg. Amennyiben valami számítás igényes logikai rész van ami túl sokáig 
blokkolja ezt a szálat, abban az esetben a következő logikai frissítésben rákorrigálunk egy úgynevezett `DeltaTime`
rendszerrel, amely azt jelenti, hogy a logikai frissítések között eltelt időt vesszük figyelembe a számításoknál.

Egy egyszerű példa a fizikából jól megszokott sebesség, út és idő összefüggésre. Ha a játékunkban egy entitás
$10$ egységnyi sebességgel mozog, és a logikai frissítések $60$-szor történnek másodpercenként, akkor az entitás
elmozdulásának a mértéke, azaz a megtett útja a sebesség és az eltelt idő szorzata lesz. Ebben az esetben az
eltelt idő $\frac{1}{60}$, és a sebesség $10$, ezért a megtett út $10 \cdot \frac{1}{60} = \frac{1}{6}$ egység lesz.

Amennyiben egy akadás miatt a következő logikai frissítés $0.1$ másodperccel később történik, akkor az eltelt idő
$0.1 + \frac{1}{60}$, és a megtett út $10 \cdot (0.1 + \frac{1}{60}) = 1.666...$ egység lesz. Ezzel máris korrigáltuk
a késést, és a játékunk továbbra is tartani fogja a Tick ráta értékét, a dolgok sebeségét és még sok mást a képkockarátától függetlenül.

Természetesen ez a módszer nem egy ezüstgolyó minden problémánkra, hiszen a játékunknak a logikai frissítések között eltelt időtől függően történhetnek olyan szélső esetek, amelyeket a játékfejlesztőknek kezelniük kell.
Ilyen például a fent említett fizikai szimuláció, amelynek a számításai a logikai frissítések között eltelt időtől 
függően változhatnak. Minél nagyobb az eltelt idő, annál pontatlanabbak lesznek a számítások, és annál nagyobb lesz a 
hiba, arról nem is beszélve, hogy mivel a fizikai szimuláció ütközésdetektáláson alapszik, amennyiben a két lépés között
akkora utat tett meg a test, hogy a másik testen átment teljesen, akkor a szimuláció nem fogja észrevenni azt.

### Eseménykezelés
> Event handling

Minden játéknak szüksége van eseménykezelésre, amely a játékosok interakcióját jelenti a játékkal. Az eseménykezelés
a játékmotorokban többféle módon is megvalósítható, amelyek közül mi kettő módszert is használni fogunk.

Az egyik módszer az, hogy a játékfejlesztőknek egy olyan függvényt kell hogy írniuk, amelyet a motor a játék
futása során fog meghívni, amikor egy esemény történik. (`Callback`) Ez a módszer nagyon egyszerű, és gyors,
de nem rugalmas, mert a játékfejlesztőknek minden esetben egy új függvényt kell hogy írniuk, amelyet a motor
meghívhat. Erre a módszerre a Retained Mode GUI-nál fogunk példát látni.

A másik módszer az, hogy a játékfejlesztőknek elérhető lesz egy Singleton osztály a motorból, amelyen keresztül
azonnal lekérdezhetik egyes események állapotát, mint például egy gomb az adott tick/képkocka alatt került lenyomásra,
felengedésre, vagy lenyomva van tartva, az egér mennyit mozgott az előző időlépés óta, és még sok más. Ez a módszer
nagyon egyszerűen implementálható, és a reszponzivitása sokkal jobb, ebből kifolyólag sokkal alkalmasabb a játék 
aktív bemeneteinek kezelésére. A UI-nál ennek ellenére szigorúan az Immediate Mode GUI fogja ezt használni, mivel
minden más esetben a másik módszer alkalmasabb.

## A kész játék fájlformátuma
> The file format of the finished game

A kész játék futtathatójának várható fájlformátuma egy `.jar` a Java nyelv miatt. A játék által felhasznált
fájlok, mint például a modellek, a textúrák, és a hangok, egy külön mappában lesznek tárolva, amelyet a játék
futtatásakor a játék a saját mappájában fog keresni. A játék futtatásához szükséges Java Runtime Environment
verziója a 11-es verzió lesz, amelyet a játék telepítésekor fogunk telepíteni, ha az nincs még telepítve.

## Geometriai és shader adatok tárolása
> Storing geometry and shader data

A játékmotorokban a geometriai és shader adatok tárolása egy nagyon fontos része a tervezésnek. Ezeket az alábbi
módon tervezzük megoldani.

### A modellek
> Models

A modellek a játékmotorokban egy olyan adatok, amelyek a játékban található objektumokat, azaz entitásokat képviselő
geometria adatokat tartalmazzák. A modelleket az alábbi formátumokban tárolja a játékmotorunk:

Az adatok többsége elsősorban a `vertexBufferObject`-ban lesz tárolva, amely egy olyan adatszerkezet, amely a
model pontjainak a pozícióját, textúra koordinátáit, és normálvektorait tartalmazza, stb. tartalmazza.
Ezentúl a modelleknek lesz egy `indexBufferObject`-ja is, amely a `vertexBufferObject`-ban található pontok
összekötésének a sorrendjét tartalmazza. 

Ezeket az objektumokat minden beállításukkal együtt tárolja egy `vertexArrayObject`, amelynek a segítségével
egy hívással tudjuk a megfelelő modell adatait a videókártyán aktívvá tenni, majd szintén egy hívással tudjuk
kirajzolni.

Jelenleg az egyetlen támogatott fájlformátum modellekhez a `.gltf` formátum lesz, amelyet majd később követ a mi
saját formátumunk is.

### A shader-ek
> Shaders

A shaderek a játékmotorokban egy olyan adatok, amelyek a játékban található objektumokat, azaz entitásokat
megjelenítő algoritmusokat tartalmazzák. A shadereket az alábbi formátumokban tárolja a játékmotorunk:

A shader rendelkezik egy a GLSL-re épülő saját extra formátummal, amely segít a motornak beazonosítani a
szükséges uniformok, layoutok, structok és egyébb szükséges adatok elhelyezkedését már betöltési időben, és
megfelelő `#pragma` operátorokkal lehetőve teszi opcionálisan engedélyezett kódrészletek létrehozását.

Bár az alapértelmezett shader az objektumok megjelenítésére a játékmotorban már megtalálható PBR shader, de
a játékfejlesztőknek lehetőségük lesz saját shader-eket is létrehozni, amelyeket a játék futása során tudnak
használni.

A shadert a `staticModelRendererComponent`-ben tároljuk, amelyet a játékfejlesztők a játékfájlokban tudnak majd megadni.
(Ez még erősen változás alatt állhat!)

A shaderek becachelésre kerülnek a fájlneveik szerint, így a játék futása során nem kell újra betölteni őket,
amennyiben már egyszer használták őket.

## Egyéb funkciók
> Other features

A motor sok nem besorolható funkcióval is rendelkezik, amelyeket a játékfejlesztők használhatnak a játékuk
fejlesztése során.

### Az ablak beállításai
> Window settings

Az ablak teljesen kontrollálható lesz a játékfejlesztők számára, hasonló módon mint a piacvezető játékmotorokban.
A játékfejlesztőknek lehetőségük lesz a következő beállításokat megadni az ablakhoz:


- Méret
- Cím
- Ikon
- VSync és/vagy Képkocka limit
- Teljes képernyő
- Ablak átméretezhetősége
- Felbontás
- stb.

### Utility osztályok
> Utility classes

A utility osztályokért tisztán az LWJGL felel, amelyeket a játékfejlesztőknek lehetőségük lesz használni.
Ilyenek például a következők:


- `Vector2f` - Egy 2D-s vektor
- `Vector3f` - Egy 3D-s vektor
- `Matrix4x4f` - Egy 4x4-es mátrix
- `Quaternion` - Egy quaternion
- `Color` - Egy szín
- stb.
Ezek az osztályok már tartalmazzák a szükséges metódusokat, számítási lehetőségeket, amelyekkel a játékfejlesztők
tudják használni azokat.

### Kamerák kezelése
> Camera handling

A `CameraComponent` osztály rendelkezik egy statikus `activeCamera` változóval, amely a jelenleg aktív kamerát
tartalmazza. Ezzel ha az aktív kezelt kamerát megszeretnénk változtatni, akkor csak egy másik kamerán meg kell
hívni a `setActiveCamera` metódust, és a motor a többi helyen már automatikusan használni fogja az új kamerát.

### QoL funkciók
> Quality of life features

A motor rendelkezni fog pár a felhasználást megkönnyítő funkcióval, amelyeket a játékfejlesztők tudnak használni.
Ilyenek például a következők:
- `FPS`, `frameTime` és egyéb `timing` adatok lekérdezése
- A logikai classok teljes elérést kapnak a `time`, `deltaTime` és `fixedDeltaTime` változókhoz
- Ezek egy IMGUI felületen is elérhetőek lesznek

# Használhatóság
> Usability

A játékmotorok használhatósága egy nagyon fontos része a tervezésnek. A játékmotoroknak a használhatóságát
a megfelelő dokumentáció, és a megfelelő felhasználói felület biztosítja. 

A megfelelő dokumentációt a nyelv és a fejlesztői környezet biztosítja, amelyeket a JavaDocs és a Java nyelv
segítségével fogunk megvalósítani.

A megfelelő felhasználói felületet a motorunk szerkesztőjében fogjuk megvalósítani, amelyet a játékfejlesztők
fognak használni. Ez a felület a játékfejlesztőknek fogja lehetővé tenni, hogy a játékukat szerkesszék, és
teszteljék a teljes motor újrakomplilálása nélkül.

## Betanulás nehézsége
> Learning curve

A játékmotorunkat úgy tervezzük, hogy a játékfejlesztőknek ne legyen nehéz megtanulni használni.
Ezért egy könnyen érthető, tisztán angol nyelvű JavaDocs dokumentációval fog rendelkezni a motor.
Ezentúl a motorhoz több példaprojekt is elérhető lesz, amelyek bemutatják a motor használatát.
A motort úgy fogjuk kialakítani, hogy hasonlítson más, már a piacon lévő játékmotorokra, így a
játékfejlesztőknek nem kell teljesen új módszereket megtanulniuk.

## Összehasonlítás más módszerekkel
> Comparison with other methods

A játékmotorunk a piacon már kitesztelt, alkalmazott és jól bevált módszereket fog alkalmazni.
Ennek köszönhetően a tanulási-görbe nem lesz nagyon meredek.
Ilyenek például a Unity, a Godot, és a Unreal Engine.

## Más alkalmazások, amelyeket használunk a fejlesztés során
> Other applications we use during development

A játékmotorunk fejlesztése során több alkalmazást is használunk, amelyek közül a legfontosabbak a következők:


- [IntelliJ IDEA](https://www.jetbrains.com/idea/) - A fejlesztői környezet
- [Git](https://git-scm.com/) - A verziókezelő rendszer
- [Blender](https://www.blender.org/) - A játékmotorunkhoz használt 3D-s modellek készítéséhez használt alkalmazás. Ingyenes, remekül támogatja a kívánt fájlformátumokat, és már a modellek anyagait is beállíthatjuk szerkesztés közben benne.
- [Adobe Photoshop](https://www.adobe.com/products/photoshop.html) - A textúrák készítéséhez
- [Adobe Illustrator](https://www.adobe.com/products/illustrator.html) - A vektorgrafikus elemek készítéséhez

# Megbízhatóság
> Reliability

Rendelkezésre állás: A teljes használhatóság korlátozott lesz abban, hogy a fejlesztőknek melyek lesznek
azok a dolgok, amelyeket maguknak kell megoldaniuk, de ez projektenként eltér szóval egységes mércét nem
tudunk alkalmazni.

MTBF: (Mean Time Between Failures) A játékmotorunknak nem lesznek olyan részei, amelyek meghibásodhatnak ideális körülmények között, így ezt a metrikát nem tudjuk alkalmazni.

MTTR: (Mean Time To Repair) Amennyiben a játékmotor hibásodik meg, a fejlesztők számára a könyvtár frissítésére
lesz szükség, amelyet a Maven Package Manager segítségével tudnak majd elvégezni. Mivel a tervezési fázis során
nem tudjuk kitapasztalni hogy mennyi időt vehetnek igénybe az esetlegesen létrejövő hibák javítása, ezért ezt a
metrikát sem tudjuk alkalmazni.

A rendszer eredményeinek pontossága, felbontása: A játékmotorunk pontosságát az fogja befolyásolni, hogy a projektben
milyen Tick-Rate -et szabunk meg, azaz milyen gyakran frissülnek a játékban elérhető rendszerek. Ezentúl a fizikai rendszert
determinisztikusnak fogjuk tervezni, azaz a játék ugyanazon a kiindulási állapotokból ugyanazokat az eredményeket fogja
produkálni.

# Teljesítmény
> Performance

A játékmotorunk teljesítménye több tényezőtől is függ, amelyek a projektenként eltérő metrikával fognak rendelkezni. Ennek ellenére a legáltalánosabb metrikákat megpróbáljuk kifejteni, és a leghatékonyabb megoldásokat alkalmazni azok a feladatok terén, amelyekért mi közvetlenül felelősek vagyunk.

## Válaszidő
> Response time

A válaszidő abban az esetben, amennyiben a kódszerkezet szerint helyesen van alkalmazva a motor, és a játékfejlesztők nem végeznek túlzottan eltérő futási időt igénylő számításokat a rendszerekben, akkor a Tick Rate -től fog függeni. Ez azt jelenti, hogy mivel a rajzolási és a logikai folyamat el van választva egymástól, ezért a játékfejlesztőknek nem kell a rajzolási folyamatot várniuk, hogy a logikai folyamat elvégezze a számításait. Ez a megoldás a legtöbb játékmotorban megtalálható, és a legtöbb játékfejlesztő is ezt a megoldást alkalmazza, mert ez a legkönnyebben használható, és a leggyorsabb megoldás.

## Áteresztőképesség
> Throughput

Ezek a metrikák ideális körülmények között a meglehetősen magas értéket fogják elérni, mivel a játékmotorunkat úgy tervezzük, hogy a játékfejlesztőknek ne kelljen túl sokat törődniük extra optimalizálással. Amennyiben megfelelően a dokumentáció szerint használják a játékmotorunkat, akkor performans marad a játék.

## Kapacitás
> Capacity

A játékmotorunk korlátait a 32-bites floating point számok határozzák meg a pályaméret során, és a 8-karakteres alfanumerikus  azonosítók a maximális entitásszám során. Ezek a korlátok hozzávetőleg megfelelnek a jelenlegi játékmotorok korlátainak. 

## Erőforrásigény
> System requirements

Erőforrás igények: A játékmotorunkat úgy próbáljuk megtervezni, hogy a lehető legkevesebb erőforrást használja. Ennek köszönhetően elérhető, hogy régebbi hardvereken, és a dedikált grafikus kártyával nem rendelkező gépeken is futtatható legyen. Természetesen az igények ennek ellenére főként még mindig az adott játékfejlesztőtől függenek.

# Támogatottság
> Supportability

A játékmotorunkat úgy próbáljuk elkészíteni, hogy maximáljuk a közösségalapú támogatottságot. Ezért majd a motor maga egy olyan licensz alatt lesz elérhető, amely lehetővé teszi a játékfejlesztőknek, hogy a motor forráskódját is elérjék, és a motor fejlesztésében részt vegyenek. Ezen kívül a motorhoz egy dokumentációt is készítünk, amely a játékfejlesztőknek kell használniuk a motor használatához. A dokumentáció a JavaDocs formátumot fogja használni, amelyet a Java nyelv biztosít. Ezen kívül a motorhoz több példaprojekt is elérhető lesz, amelyek bemutatják a motor használatát. A motorhoz egy Discord szerver is tartozni fog, amelyen a játékfejlesztők segítséget kérhetnek, és a motor fejlesztői is segítséget nyújthatnak. A motorhoz egy GitHub repository is tartozni fog, amelyen a fejlesztők hibákat jelenthetnek, és a motor fejlesztői javíthatják azokat. A motorhoz egy Maven Package is tartozni fog, amelyen keresztül a játékfejlesztők a motor frissítéseit tudják elérni. A motorhoz egy hivatalos weboldal is tartozni fog, amelyen keresztül a fejlesztők elérhetik a motorhoz tartozó dokumentációt, példaprojekteket, a motorhoz tartozó Discord szervert, és GitHub repositoryt.

A közösség alapú támogatottság azért fontos számunkra, mert a motor könnyű bővíthetőséget/modolhatóságot próbál bizosítani egy "out-of-box", azaz már kezdéstől fogva készen. Mindez akkor elérhető, ha a közösség megfelelő hozzáféréssel rendelkezik a játék kódszerkezetéhez.

## Elnevezési konvenciók
> Naming conventions

A játékmotorunkban a következő elnevezési konvenciókat fogjuk alkalmazni:


- A változók nevei a camelCase konvenciót fogják követni, amely azt jelenti, hogy a változó nevekben a szavakat össze kell fűzni, és a szavak első betűit nagybetűvel kell írni, kivéve az első szót, amelyet kisbetűvel kell írni.
- Az osztályok és metódusok nevei a PascalCase konvenciót fogják követni, amely azt jelenti, hogy a szavakat össze kell fűzni, és a szavak első betűit nagybetűvel kell írni minden szó esetén.

## Üzemeltetés
> Operability

A játéknak a megfelelő Java verzióra lesz szüksége azon gépeken, amelyeken futtatni kívánjuk.
Ezen kívül a játékmotorunknak a megfelelő OpenGL verzióra lesz szüksége, amelyet az Apple gépek korlátai miatt a 3.3-ra fogunk korlátozni.

## Naplózás
> Logging

A játékmotorunk rendelkezni fog egy Logger osztállyal, amely a játékfejlesztők számára lehetővé teszi
a játék futása során fellépő hibák naplózását. Ezen naplókat hibajelentés céljából el lehet majd küldeni a
játékfejlesztőknek.

# Tervezési korlátozások
> Design constraints

A játékmotorunkat Java nyelven fogjuk megírni, amelynek köszönhetően a játékfejlesztőknek nem kell
külön platformokra külön-külön megírniuk a játékot. Viszont ez a korlát azt is jelenti, hogy a játék
nem lesz olyan gyors, mint egy C++ nyelven megírt játék, illetve nem minden mobil platformot fog támogatni.

A játékmotorunkat OpenGL használatával készítjük, amely elérhető rengeteg eszközön, viszont a vele elérhető maximális teljesítmény nem olyan magas, mint például a Vulkan API-é.

Az ECS rendszer azt igényli, hogy a motor újrafordításra kerüljön minden egyes új komponens hozzáadásakor.
Ez a korlát azt jelenti, hogy a játékfejlesztőknek nem lesz lehetőségük a játék futása során új komponenseket
létrehozni a játékhoz, ezentúl ez lassíthatja a játékfejlesztést.
Ezt majd a jövőben megpróbáljuk kiküszöbölni egy szkriptelhető rendszerrel, amely lehetővé teszi a játékfejlesztőknek a játék futása során új komponensek létrehozását. A nyelv a szkripteléshez várhatóan Lua lesz.

A játékmotorunkat úgy tervezzük, hogy a játékfejlesztőknek ne kelljen túl sokat törődniük a motor optimalizálásával. Ez viszont azt jelenti, hogy a játékfejlesztőknek nem lesz lehetőségük a motor teljesítményét maximalizálni, és a játékfejlesztőknek a játékukat a motor korlátaihoz kell igazítaniuk.

# Online dokumentáció
> Online documentation

A játékmotorunkhoz egy online dokumentáció lesz elérhető, amelyet elsősorban JavaDocs formájában fogunk
biztosítani. Ezen kívül a játékmotorunkhoz több példaprojekt is elérhető lesz, amelyek bemutatják a
motor használatát.

# Felhasznált kész komponensek
> Used third-party components

A játékmotorunk több könyvtárt is alkalazni fog, amelyeket a Maven Package Manager segítségével fogunk
elérni. Ezek a könyvtárak a következők:


- [LWJGL](https://www.lwjgl.org/) - A játékmotorunk alapját képező könyvtár, amely a játék ablakot, és a grafikus API-t biztosítja. Ezentúl alkalmazzuk benne az ASSIMP és STBI lehetőségeket is.
- [GSON](https://github.com/google/gson) - A játékmotorunkhoz tartozó fájlok betöltését és mentését biztosító könyvtár.
Az LWJGL a következő licenc alatt érhető el: [LWJGL License]("https://www.lwjgl.org/license")
Ennek köszönhetően a játékmotorunk ingyenesen alkalmazhatja ezt a könyvtárat.
A GSON a következő licenc alatt érhető el: [Apache-2.0 license]("https://github.com/google/gson/blob/main/LICENSE")
Ez a licensz is ingyenesen alkalmazható a játékmotorunkban.

# Interfészek
> Interfaces

A játékmotorunkban több interfész is lesz, amelyek a felhasználói hozzáférésért, és a modolhatóságért felelősek.

## Felhasználói interfészek
> User interfaces

A játékmotorunknak két felhasználói interfésze lesz, amelyek a következők:


- A játékfejlesztőknek szánt szerkesztői felület, azaz a motor szerkesztője amelyet az Intermediate Mode GUI segítségével fogunk megvalósítani.
- A játékosoknak szánt felhasználói felület, amelyet a Retained Mode GUI segítségével fognak megvalósítani a játékfejlesztők.

## Modolhatósági interfészek
> Modding interfaces

A játékmotorunknak több modolhatósági interfésze is lesz, amelyek a következők:


- A JSON fájlok megváltoztatása, bővítése amely a játék által tárolt adatokat tartalmazza. Lesz egy rendszer, amely engedélyezi hogy az eredeti fájlok lecserélése helyett a már meglévő fájlokat patch-ekkel módosítsuk.
- A (várható) Lua nyelvű szkriptek megváltoztatása, bővítése amely a játék logikáját tartalmazza. Lesz egy rendszer, amely engedélyezi hogy az eredeti szkriptek lecserélése helyett a már meglévő szkripteket patch-ekkel módosítsuk, illetve akár teljesen új szkripteket is hozzáadhassunk a játékhoz.

## Hardver interfészek
> Hardware interfaces

A játékmotorunk hardver iterfészek terén az OpenGL-t fogja használni, amely a legtöbb eszközön elérhető.
Ezentúl egy alapvető, a futtatható alkalmazás mappájában történő fájlkezelőt fogunk biztosítani a játékfejlesztők
számára, amely a játékban használt fájlok kezelésére lesz képes.

# Alkalmazott szabványok
> Applied standards

A játékmotorunk nagyon sok szabványt fog alkalmazni, amelyeket a játékfejlesztőknek is ajánlott lesz alkalmazniuk. Ezek a szabványok az ipari és piaci megállapodások, a kódolási konvenciók, és a szabványosított fájlformátumokból erednek.

## Kötelezően alkalmazandó szabványok
> Mandatory standards

A játékmotorunkban a következő szabványokat fogjuk alkalmazni:


- [Java Coding Conventions](https://www.oracle.com/java/technologies/javase/codeconventions-introduction.html) - A Java nyelvhez tartozó kódolási konvenciók.
- [Java Naming Conventions](https://www.oracle.com/java/technologies/javase/codeconventions-namingconventions.html) - A Java nyelvhez tartozó elnevezési konvenciók.
- [OpenGL Coding Conventions](https://www.khronos.org/opengl/wiki/Main_Page) - Az OpenGL API-hoz tartozó kódolási konvenciók.
- [JSON](https://www.json.org/json-en.html) - A JSON fájlokhoz tartozó szabvány.
- [GLTF](https://www.khronos.org/gltf/) - A GLTF fájlokhoz tartozó szabvány.
- [Lua](https://www.lua.org/) - A Lua nyelvhez tartozó szabvány.

## Választás alapján alkalmazandó szabványok
> Choice-based standards

A játékmotorunkban a következő szabványokat fogjuk alkalmazni:


- [ECS](https://en.wikipedia.org/wiki/Entity_component_system) - Az Entity-Component-System tervezési minta.
- [PBR](https://en.wikipedia.org/wiki/Physically_based_rendering) - A Physically Based Rendering technika.
- [Determinisztikus fizikai rendszer](https://en.wikipedia.org/wiki/Deterministic_finite_automaton) - A fizikai rendszer determinisztikus működése.

# Mellékletek
> Attachments

Képek egy már működő prototípusról:

![01.]{512px}{100%}(img/prototype1.png)

Egy ábra a PBR anyagról. Balról jobbra haladva a fémesség nő, felülről lefelé haladva a simaság.

![02.]{512px}{100%}(img/prototype2.png)

Egy shell texturing segítségével készült szőr rajzolási teszt.

![03.]{512px}{100%}(img/prototype3.png)

Egy bonyolultabb pályáról készült megvilágítási teszt.

![04.]{512px}{100%}(img/abra.png)

A motorban már elérhető és jelenleg használt fontosabb objektumok.