import json
import os
import glob

translations = {
    "en_us": {
        "undertale_death_screen.config.vanilla_fade_in": "Vanilla Fade In?",
        "undertale_death_screen.config.vanilla_fade_in.ttp": "Whether the normal death screen fades in smoothly after shattering.",
        "undertale_death_screen.config.vanilla_fade_in_duration": "Fade In Duration",
        "undertale_death_screen.config.vanilla_fade_in_duration.ttp": "How long the fade takes in ticks (20 = 1 second)."
    },
    "ru_ru": {
        "undertale_death_screen.config.vanilla_fade_in": "Плавный переход к ванилле?",
        "undertale_death_screen.config.vanilla_fade_in.ttp": "Должен ли плавно появляться обычный экран смерти после разбивания.",
        "undertale_death_screen.config.vanilla_fade_in_duration": "Длительность перехода",
        "undertale_death_screen.config.vanilla_fade_in_duration.ttp": "Длительность плавного появления в тиках (20 = 1 секунда)."
    },
    "uk_ua": {
        "undertale_death_screen.config.vanilla_fade_in": "Плавний перехід до ваніли?",
        "undertale_death_screen.config.vanilla_fade_in.ttp": "Чи має плавно з'являтися звичайний екран смерті після розбиття.",
        "undertale_death_screen.config.vanilla_fade_in_duration": "Тривалість переходу",
        "undertale_death_screen.config.vanilla_fade_in_duration.ttp": "Тривалість плавного з'явлення у тіках (20 = 1 секунда)."
    },
    "de_de": {
        "undertale_death_screen.config.vanilla_fade_in": "Sanfter Übergang zu Vanilla?",
        "undertale_death_screen.config.vanilla_fade_in.ttp": "Ob der normale Todesbildschirm nach dem Zersplittern sanft eingeblendet werden soll.",
        "undertale_death_screen.config.vanilla_fade_in_duration": "Übergangsdauer",
        "undertale_death_screen.config.vanilla_fade_in_duration.ttp": "Dauer des Einblendens in Ticks (20 = 1 Sekunde)."
    },
    "fr_fr": {
        "undertale_death_screen.config.vanilla_fade_in": "Fondu vers Vanilla ?",
        "undertale_death_screen.config.vanilla_fade_in.ttp": "Si l'écran de mort normal apparaît progressivement après le bris.",
        "undertale_death_screen.config.vanilla_fade_in_duration": "Durée du fondu",
        "undertale_death_screen.config.vanilla_fade_in_duration.ttp": "Durée du fondu en ticks (20 = 1 seconde)."
    },
    "es_es": {
        "undertale_death_screen.config.vanilla_fade_in": "¿Transición suave a Vanilla?",
        "undertale_death_screen.config.vanilla_fade_in.ttp": "Si la pantalla de muerte normal aparece gradualmente después de romperse.",
        "undertale_death_screen.config.vanilla_fade_in_duration": "Duración de transición",
        "undertale_death_screen.config.vanilla_fade_in_duration.ttp": "Duración de la aparición gradual en tics (20 = 1 segundo)."
    },
    "zh_cn": {
        "undertale_death_screen.config.vanilla_fade_in": "原版淡入？",
        "undertale_death_screen.config.vanilla_fade_in.ttp": "破碎后是否平滑淡入原版的死亡界面。",
        "undertale_death_screen.config.vanilla_fade_in_duration": "淡入时长",
        "undertale_death_screen.config.vanilla_fade_in_duration.ttp": "淡入的时长（单位为刻，20刻=1秒）。"
    },
    "ja_jp": {
        "undertale_death_screen.config.vanilla_fade_in": "バニラへのフェードイン？",
        "undertale_death_screen.config.vanilla_fade_in.ttp": "砕け散った後、通常の死亡画面が滑らかにフェードインするかどうか。",
        "undertale_death_screen.config.vanilla_fade_in_duration": "フェードイン時間",
        "undertale_death_screen.config.vanilla_fade_in_duration.ttp": "フェードインの長さ（ティック単位、20 = 1秒）。"
    },
    "ko_kr": {
        "undertale_death_screen.config.vanilla_fade_in": "바닐라 페이드 인?",
        "undertale_death_screen.config.vanilla_fade_in.ttp": "조각난 후 기본 사망 화면이 부드럽게 나타날지 여부.",
        "undertale_death_screen.config.vanilla_fade_in_duration": "페이드 인 지속 시간",
        "undertale_death_screen.config.vanilla_fade_in_duration.ttp": "페이드 인 지속 시간 (틱 단위, 20 = 1초)."
    },
    "it_it": {
        "undertale_death_screen.config.vanilla_fade_in": "Dissolvenza verso Vanilla?",
        "undertale_death_screen.config.vanilla_fade_in.ttp": "Se la normale schermata di morte appare gradualmente dopo l'infrangersi.",
        "undertale_death_screen.config.vanilla_fade_in_duration": "Durata dissolvenza",
        "undertale_death_screen.config.vanilla_fade_in_duration.ttp": "Durata della dissolvenza in tick (20 = 1 secondo)."
    },
    "pt_br": {
        "undertale_death_screen.config.vanilla_fade_in": "Transição suave para Vanilla?",
        "undertale_death_screen.config.vanilla_fade_in.ttp": "Se a tela de morte normal aparece suavemente após quebrar.",
        "undertale_death_screen.config.vanilla_fade_in_duration": "Duração da transição",
        "undertale_death_screen.config.vanilla_fade_in_duration.ttp": "Duração do surgimento em ticks (20 = 1 segundo)."
    },
    "pt_pt": {
        "undertale_death_screen.config.vanilla_fade_in": "Transição suave para Vanilla?",
        "undertale_death_screen.config.vanilla_fade_in.ttp": "Se o ecrã de morte normal aparece suavemente após estilhaçar.",
        "undertale_death_screen.config.vanilla_fade_in_duration": "Duração da transição",
        "undertale_death_screen.config.vanilla_fade_in_duration.ttp": "Duração da transição em ticks (20 = 1 segundo)."
    },
    "pl_pl": {
        "undertale_death_screen.config.vanilla_fade_in": "Płynne przejście do Vanilla?",
        "undertale_death_screen.config.vanilla_fade_in.ttp": "Czy normalny ekran śmierci ma pojawiać się płynnie po roztrzaskaniu.",
        "undertale_death_screen.config.vanilla_fade_in_duration": "Czas przejścia",
        "undertale_death_screen.config.vanilla_fade_in_duration.ttp": "Czas płynnego pojawienia się w tickach (20 = 1 sekunda)."
    },
    "tr_tr": {
        "undertale_death_screen.config.vanilla_fade_in": "Vanilla'ya yumuşak geçiş?",
        "undertale_death_screen.config.vanilla_fade_in.ttp": "Kırıldıktan sonra normal ölüm ekranının yumuşak bir şekilde belirip belirmeyeceği.",
        "undertale_death_screen.config.vanilla_fade_in_duration": "Geçiş Süresi",
        "undertale_death_screen.config.vanilla_fade_in_duration.ttp": "Geçişin tick cinsinden süresi (20 = 1 saniye)."
    },
    "nl_nl": {
        "undertale_death_screen.config.vanilla_fade_in": "Vloeiende overgang naar Vanilla?",
        "undertale_death_screen.config.vanilla_fade_in.ttp": "Of het normale doodsscherm vloeiend verschijnt na het verbrijzelen.",
        "undertale_death_screen.config.vanilla_fade_in_duration": "Overgangsduur",
        "undertale_death_screen.config.vanilla_fade_in_duration.ttp": "Hoe lang het verschijnen duurt in ticks (20 = 1 seconde)."
    },
    "ar_sa": {
        "undertale_death_screen.config.vanilla_fade_in": "انتقال سلس إلى فانيلا؟",
        "undertale_death_screen.config.vanilla_fade_in.ttp": "ما إذا كانت شاشة الموت العادية تظهر بسلاسة بعد التحطم.",
        "undertale_death_screen.config.vanilla_fade_in_duration": "مدة الانتقال",
        "undertale_death_screen.config.vanilla_fade_in_duration.ttp": "مدة التلاشي بالقرادات (20 = 1 ثانية)."
    },
    "vi_vn": {
        "undertale_death_screen.config.vanilla_fade_in": "Chuyển dần sang Vanilla?",
        "undertale_death_screen.config.vanilla_fade_in.ttp": "Cho dù màn hình chết bình thường hiện ra mượt mà sau khi vỡ vụn.",
        "undertale_death_screen.config.vanilla_fade_in_duration": "Thời lượng xuất hiện",
        "undertale_death_screen.config.vanilla_fade_in_duration.ttp": "Thời lượng làm mờ bằng tick (20 = 1 giây)."
    },
    "th_th": {
        "undertale_death_screen.config.vanilla_fade_in": "จางเข้าสู่หน้าจอ Vanilla?",
        "undertale_death_screen.config.vanilla_fade_in.ttp": "หน้าจอการตายปกติจะค่อยๆ ปรากฏขึ้นอย่างนุ่มนวลหลังจากการแตกสลายหรือไม่",
        "undertale_death_screen.config.vanilla_fade_in_duration": "ระยะเวลาการจาง",
        "undertale_death_screen.config.vanilla_fade_in_duration.ttp": "ระยะเวลาการจางในหน่วยติ๊ก (20 = 1 วินาที)"
    },
    "sv_se": {
        "undertale_death_screen.config.vanilla_fade_in": "Fade in till Vanilla?",
        "undertale_death_screen.config.vanilla_fade_in.ttp": "Om den vanliga dödsskärmen mjukt tonas in efter krossandet.",
        "undertale_death_screen.config.vanilla_fade_in_duration": "Toningens Längd",
        "undertale_death_screen.config.vanilla_fade_in_duration.ttp": "Toningens längd i ticks (20 = 1 sekund)."
    },
    "cs_cz": {
        "undertale_death_screen.config.vanilla_fade_in": "Plynulý přechod do Vanilly?",
        "undertale_death_screen.config.vanilla_fade_in.ttp": "Zda se po roztříštění plynule objeví normální obrazovka smrti.",
        "undertale_death_screen.config.vanilla_fade_in_duration": "Délka přechodu",
        "undertale_death_screen.config.vanilla_fade_in_duration.ttp": "Jak dlouho trvá prolínání v ticích (20 = 1 sekunda)."
    },
    "hu_hu": {
        "undertale_death_screen.config.vanilla_fade_in": "Fokozatos átmenet a Vanillára?",
        "undertale_death_screen.config.vanilla_fade_in.ttp": "A normál halál képernyő fokozatosan jelenjen-e meg az összetörés után.",
        "undertale_death_screen.config.vanilla_fade_in_duration": "Átmenet hossza",
        "undertale_death_screen.config.vanilla_fade_in_duration.ttp": "Az átmenet hossza tickekben (20 = 1 másodperc)."
    },
    "ro_ro": {
        "undertale_death_screen.config.vanilla_fade_in": "Tranziție lentă la Vanilla?",
        "undertale_death_screen.config.vanilla_fade_in.ttp": "Dacă ecranul normal de moarte apare treptat după sfărâmare.",
        "undertale_death_screen.config.vanilla_fade_in_duration": "Durata tranziției",
        "undertale_death_screen.config.vanilla_fade_in_duration.ttp": "Durata estompării în tick-uri (20 = 1 secundă)."
    },
    "da_dk": {
        "undertale_death_screen.config.vanilla_fade_in": "Fade ind til Vanilla?",
        "undertale_death_screen.config.vanilla_fade_in.ttp": "Om den normale dødsskærm glidende tones ind efter splintringen.",
        "undertale_death_screen.config.vanilla_fade_in_duration": "Fading Længde",
        "undertale_death_screen.config.vanilla_fade_in_duration.ttp": "Længden af toningen i ticks (20 = 1 sekund)."
    },
    "fi_fi": {
        "undertale_death_screen.config.vanilla_fade_in": "Häivytys Vanillaan?",
        "undertale_death_screen.config.vanilla_fade_in.ttp": "Ilmestyykö normaali kuolinruutu pehmeästi särkymisen jälkeen.",
        "undertale_death_screen.config.vanilla_fade_in_duration": "Häivytyksen Kesto",
        "undertale_death_screen.config.vanilla_fade_in_duration.ttp": "Häivytyksen kesto tickeinä (20 = 1 sekunti)."
    },
    "no_no": {
        "undertale_death_screen.config.vanilla_fade_in": "Fade inn til Vanilla?",
        "undertale_death_screen.config.vanilla_fade_in.ttp": "Om den vanlige dødsskjermen glidende tones inn etter knusingen.",
        "undertale_death_screen.config.vanilla_fade_in_duration": "Toningslengde",
        "undertale_death_screen.config.vanilla_fade_in_duration.ttp": "Toningens lengde i ticks (20 = 1 sekund)."
    },
    "el_gr": {
        "undertale_death_screen.config.vanilla_fade_in": "Ομαλή μετάβαση σε Vanilla;",
        "undertale_death_screen.config.vanilla_fade_in.ttp": "Εάν η κανονική οθόνη θανάτου εμφανίζεται ομαλά μετά το σπάσιμο.",
        "undertale_death_screen.config.vanilla_fade_in_duration": "Διάρκεια Μετάβασης",
        "undertale_death_screen.config.vanilla_fade_in_duration.ttp": "Η διάρκεια της εμφάνισης σε ticks (20 = 1 δευτερόλεπτο)."
    },
    "bg_bg": {
        "undertale_death_screen.config.vanilla_fade_in": "Плавен преход към Vanilla?",
        "undertale_death_screen.config.vanilla_fade_in.ttp": "Дали нормалният екран при смърт плавно се появява след счупването.",
        "undertale_death_screen.config.vanilla_fade_in_duration": "Продължителност на прехода",
        "undertale_death_screen.config.vanilla_fade_in_duration.ttp": "Продължителността на плавното появяване в тикове (20 = 1 секунда)."
    },
    "sk_sk": {
        "undertale_death_screen.config.vanilla_fade_in": "Plynulý prechod do Vanilly?",
        "undertale_death_screen.config.vanilla_fade_in.ttp": "Či sa po roztrieštení plynulo objaví normálna obrazovka smrti.",
        "undertale_death_screen.config.vanilla_fade_in_duration": "Dĺžka prechodu",
        "undertale_death_screen.config.vanilla_fade_in_duration.ttp": "Ako dlho trvá prelínanie v tikoch (20 = 1 sekunda)."
    },
    "hr_hr": {
        "undertale_death_screen.config.vanilla_fade_in": "Blagi prijelaz na Vanillu?",
        "undertale_death_screen.config.vanilla_fade_in.ttp": "Da li se normalni ekran smrti polako prikazuje nakon razbijanja.",
        "undertale_death_screen.config.vanilla_fade_in_duration": "Trajanje prijelaza",
        "undertale_death_screen.config.vanilla_fade_in_duration.ttp": "Trajanje pojavljivanja u tickovima (20 = 1 sekunda)."
    },
    "sr_rs": {
        "undertale_death_screen.config.vanilla_fade_in": "Благи прелаз на Vanillu?",
        "undertale_death_screen.config.vanilla_fade_in.ttp": "Да ли се нормални екран смрти полако приказује након разбијања.",
        "undertale_death_screen.config.vanilla_fade_in_duration": "Трајање прелаза",
        "undertale_death_screen.config.vanilla_fade_in_duration.ttp": "Трајање појављивања у тиковима (20 = 1 секунда)."
    },
    "id_id": {
        "undertale_death_screen.config.vanilla_fade_in": "Pudar perlahan ke Vanilla?",
        "undertale_death_screen.config.vanilla_fade_in.ttp": "Apakah layar kematian normal muncul dengan mulus setelah hancur.",
        "undertale_death_screen.config.vanilla_fade_in_duration": "Durasi Pudar",
        "undertale_death_screen.config.vanilla_fade_in_duration.ttp": "Berapa lama pudarnya dalam ticks (20 = 1 detik)."
    },
    "en_ud": {
        "undertale_death_screen.config.vanilla_fade_in": "¡¿uI ǝpɐℲ ɐllıuɐɅ",
        "undertale_death_screen.config.vanilla_fade_in.ttp": "˙ƃuıɹǝʇʇɐɥs ɹǝʇɟɐ ʎlɥʇooɯs uı sǝpɐɟ uǝǝɹɔs ɥʇɐǝp lɐɯɹou ǝɥʇ ɹǝɥʇǝɥM",
        "undertale_death_screen.config.vanilla_fade_in_duration": "uoıʇɐɹnᗡ uI ǝpɐℲ",
        "undertale_death_screen.config.vanilla_fade_in_duration.ttp": "˙(puoɔǝs ⇂ = 0ᄅ) sʞɔıʇ uı sǝʞɐʇ ǝpɐɟ ǝɥʇ ƃuol ʍoH"
    },
    "tok": {
        "undertale_death_screen.config.vanilla_fade_in": "o lukin e lipu moli majuna?",
        "undertale_death_screen.config.vanilla_fade_in.ttp": "sina pakala la, lipu moli majuna li kama lukin.",
        "undertale_death_screen.config.vanilla_fade_in_duration": "tenpo pi lipu moli",
        "undertale_death_screen.config.vanilla_fade_in_duration.ttp": "tenpo pi kama lukin pi lipu moli (20 = 1 tenpo lili)."
    },
    "tlh_aa": {
        "undertale_death_screen.config.vanilla_fade_in": "Vanilla pIv?",
        "undertale_death_screen.config.vanilla_fade_in.ttp": "ghorDI' tIq, Vanilla Hegh much pIv nargh.",
        "undertale_death_screen.config.vanilla_fade_in_duration": "pIv poH",
        "undertale_death_screen.config.vanilla_fade_in_duration.ttp": "pIv poH 'ut (20 = 1 tup)."
    },
    "qya_aa": {
        "undertale_death_screen.config.vanilla_fade_in": "Cala yá Vanilla?",
        "undertale_death_screen.config.vanilla_fade_in.ttp": "I firil cala tuluva yá rácë.",
        "undertale_death_screen.config.vanilla_fade_in_duration": "Lintië cala",
        "undertale_death_screen.config.vanilla_fade_in_duration.ttp": "Lintië i cala (20 = 1 lú)."
    },
    "jbo_en": {
        "undertale_death_screen.config.vanilla_fade_in": "xu binxo lo manku?",
        "undertale_death_screen.config.vanilla_fade_in.ttp": "xu lo mrobi'o cu binxo ba lo spisa",
        "undertale_death_screen.config.vanilla_fade_in_duration": "lo nilsu'a pe lo binxo",
        "undertale_death_screen.config.vanilla_fade_in_duration.ttp": "lo nilsu'a pe lo binxo (20 = 1 snidu)"
    },
    "en_an": {
        "undertale_death_screen.config.vanilla_fade_in": "Olden Death Screen?",
        "undertale_death_screen.config.vanilla_fade_in.ttp": "Whether the olden death screen dawns smoothly after shattering.",
        "undertale_death_screen.config.vanilla_fade_in_duration": "Dawns Swiftness",
        "undertale_death_screen.config.vanilla_fade_in_duration.ttp": "How long the dawning takes in ticks (20 = 1 heartbeat)."
    },
    "eo_uy": {
        "undertale_death_screen.config.vanilla_fade_in": "Apero de Vanila?",
        "undertale_death_screen.config.vanilla_fade_in.ttp": "Ĉu la normala mort-ekrano mole aperas post la rompiĝo.",
        "undertale_death_screen.config.vanilla_fade_in_duration": "Daŭro de apero",
        "undertale_death_screen.config.vanilla_fade_in_duration.ttp": "Daŭro de la apero en tikoj (20 = 1 sekundo)."
    },
    "la_la": {
        "undertale_death_screen.config.vanilla_fade_in": "Apparitio in Vanilla?",
        "undertale_death_screen.config.vanilla_fade_in.ttp": "An velum mortis usitatum clarescat lente post frangendum.",
        "undertale_death_screen.config.vanilla_fade_in_duration": "Mora apparitionis",
        "undertale_death_screen.config.vanilla_fade_in_duration.ttp": "Quantum morae sit in apparitione (20 = 1 secunda)."
    },
    "sjn": {
        "undertale_death_screen.config.vanilla_fade_in": "Cala Vanilla?",
        "undertale_death_screen.config.vanilla_fade_in.ttp": "I firil cala tuluva yá rácë.",
        "undertale_death_screen.config.vanilla_fade_in_duration": "Lintië cala",
        "undertale_death_screen.config.vanilla_fade_in_duration.ttp": "Lintië i cala (20 = 1 lú)."
    },
    "lol_us": {
        "undertale_death_screen.config.vanilla_fade_in": "NORMAL DED SKREEN?",
        "undertale_death_screen.config.vanilla_fade_in.ttp": "BRING BAK DA NORMAL DED SKREEN WEN HART GO BOOM.",
        "undertale_death_screen.config.vanilla_fade_in_duration": "HOW LONG TO COME BAK",
        "undertale_death_screen.config.vanilla_fade_in_duration.ttp": "HOW LONG IT TAYK IN TIKS (20 = 1 SEKUND)."
    },
    "en_pt": {
        "undertale_death_screen.config.vanilla_fade_in": "Vanilla Dawning?",
        "undertale_death_screen.config.vanilla_fade_in.ttp": "Whether the normal death screen dawns slowly after shatterin'.",
        "undertale_death_screen.config.vanilla_fade_in_duration": "Dawning Tides",
        "undertale_death_screen.config.vanilla_fade_in_duration.ttp": "How long the dawining takes in tides (20 = 1 second)."
    },
    "en_ws": {
        "undertale_death_screen.config.vanilla_fade_in": "Vanilla Dawning?",
        "undertale_death_screen.config.vanilla_fade_in.ttp": "Whether the normal death screen dawns smoothly after shattering.",
        "undertale_death_screen.config.vanilla_fade_in_duration": "Dawning Duration",
        "undertale_death_screen.config.vanilla_fade_in_duration.ttp": "How long the dawning takes in ticks (20 = 1 second)."
    }
}

lang_dir = r"d:\lin\Projects\MC\udsc\src\main\resources\assets\undertale_death_screen\lang"
for filepath in glob.glob(os.path.join(lang_dir, "*.json")):
    filename = os.path.basename(filepath)
    lang_code = filename.split('.')[0]
    
    with open(filepath, 'r', encoding='utf-8') as f:
        data = json.load(f)
        
    fallback = translations.get("en_us")
    to_add = translations.get(lang_code, fallback)
    
    for k, v in to_add.items():
        data[k] = v
        
    with open(filepath, 'w', encoding='utf-8') as f:
        json.dump(data, f, indent=2, ensure_ascii=False)
        f.write('\n')
        
print("Updated all translations successfully.")
