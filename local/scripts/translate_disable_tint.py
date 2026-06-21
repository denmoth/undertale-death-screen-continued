import os
import json
import urllib.request
import urllib.parse
import time

LANG_DIR = r"d:\lin\Projects\MC\udsc\common\src\main\resources\assets\undertale_death_screen\lang"

def translate_text(text, target_lang):
    if target_lang == "en_us":
        return text
    
    # Map minecraft lang codes to google translate lang codes
    lang_map = {
        "ar_sa": "ar", "bg_bg": "bg", "cs_cz": "cs", "da_dk": "da", "de_de": "de", "el_gr": "el",
        "en_an": "en", "en_pt": "en", "en_ud": "en", "en_ws": "en", "eo_uy": "eo", "es_es": "es",
        "fi_fi": "fi", "fr_fr": "fr", "hr_hr": "hr", "hu_hu": "hu", "id_id": "id", "it_it": "it",
        "ja_jp": "ja", "jbo_en": "en", "ko_kr": "ko", "la_la": "la", "lol_us": "en", "nl_nl": "nl",
        "no_no": "no", "pl_pl": "pl", "pt_br": "pt", "pt_pt": "pt", "qya_aa": "fi", "ro_ro": "ro",
        "ru_ru": "ru", "sjn": "en", "sk_sk": "sk", "sr_rs": "sr", "sv_se": "sv", "th_th": "th",
        "tlh_aa": "en", "tok": "en", "tr_tr": "tr", "uk_ua": "uk", "vi_vn": "vi", "zh_cn": "zh-CN"
    }
    
    tl = lang_map.get(target_lang.lower().split('.')[0], "en")
    if tl == "en":
        return text
        
    url = f"https://translate.googleapis.com/translate_a/single?client=gtx&sl=en&tl={tl}&dt=t&q={urllib.parse.quote(text)}"
    try:
        req = urllib.request.Request(url, headers={'User-Agent': 'Mozilla/5.0'})
        with urllib.request.urlopen(req) as response:
            result = json.loads(response.read().decode('utf-8'))
            return "".join([x[0] for x in result[0]])
    except Exception as e:
        print(f"Failed to translate to {tl}: {e}")
        return text

def main():
    base_title = "Disable Vanilla Red Tint [Experimental]"
    base_desc = "Disables the vanilla red tint around the edges of the screen after death animation."
    
    for filename in os.listdir(LANG_DIR):
        if not filename.endswith(".json"):
            continue
            
        filepath = os.path.join(LANG_DIR, filename)
        lang_code = filename.replace(".json", "")
        
        with open(filepath, 'r', encoding='utf-8') as f:
            try:
                data = json.load(f)
            except:
                continue
                
        old_key_title = "undertale_death_screen.config.vanilla_red_tint"
        old_key_desc = "undertale_death_screen.config.vanilla_red_tint.ttp"
        new_key_title = "undertale_death_screen.config.disable_vanilla_red_tint"
        new_key_desc = "undertale_death_screen.config.disable_vanilla_red_tint.ttp"
        
        needs_save = False
        
        if old_key_title in data or new_key_title not in data:
            if lang_code == "ru_ru":
                title_trans = "Отключить ванильное красное свечение [Экспериментально]"
                desc_trans = "Отключает ванильное красное свечение по краям экрана после анимации смерти."
            else:
                title_trans = translate_text(base_title, lang_code)
                desc_trans = translate_text(base_desc, lang_code)
                time.sleep(0.5) # rate limit prevention
                
            if old_key_title in data:
                del data[old_key_title]
            if old_key_desc in data:
                del data[old_key_desc]
                
            data[new_key_title] = title_trans
            data[new_key_desc] = desc_trans
            needs_save = True
            
        if needs_save:
            with open(filepath, 'w', encoding='utf-8') as f:
                json.dump(data, f, ensure_ascii=False, indent=2)
            print(f"Updated {filename}")

if __name__ == "__main__":
    main()
