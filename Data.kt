package com.qibla.noor

class Zikr(val arabic: String, val urdu: String, val english: String)

val AZKAR = listOf(
    Zikr("سُبْحَانَ اللَّهِ وَبِحَمْدِهِ", "اللہ پاک ہے، اپنی تعریف کے ساتھ", "Glory be to Allah and praise Him"),
    Zikr("سُبْحَانَ اللَّهِ الْعَظِيمِ", "پاک ہے اللہ عظمت والا", "Glory be to Allah, the Magnificent"),
    Zikr("أَسْتَغْفِرُ اللَّهَ", "میں اللہ سے بخشش مانگتا ہوں", "I seek forgiveness from Allah"),
    Zikr("لَا إِلَهَ إِلَّا اللَّهُ", "اللہ کے سوا کوئی معبود نہیں", "There is no god but Allah"),
    Zikr("لَا حَوْلَ وَلَا قُوَّةَ إِلَّا بِاللَّهِ", "گناہ سے بچنے کی طاقت اور نیکی کی قوت اللہ ہی کی طرف سے ہے", "There is no power or strength except with Allah"),
    Zikr("اللَّهُمَّ صَلِّ عَلَى مُحَمَّدٍ", "اے اللہ! محمد ﷺ پر رحمت نازل فرما", "O Allah, send blessings upon Muhammad ﷺ"),
    Zikr("الْحَمْدُ لِلَّهِ", "تمام تعریفیں اللہ کے لیے ہیں", "All praise is for Allah"),
    Zikr("اللَّهُ أَكْبَرُ", "اللہ سب سے بڑا ہے", "Allah is the Greatest")
)

/** "Arabic|Transliteration" */
val NAMES = listOf(
    "الرَّحْمَنُ|Ar-Rahman", "الرَّحِيمُ|Ar-Raheem", "الْمَلِكُ|Al-Malik", "الْقُدُّوسُ|Al-Quddus",
    "السَّلَامُ|As-Salam", "الْمُؤْمِنُ|Al-Mu'min", "الْمُهَيْمِنُ|Al-Muhaymin", "الْعَزِيزُ|Al-Aziz",
    "الْجَبَّارُ|Al-Jabbar", "الْمُتَكَبِّرُ|Al-Mutakabbir", "الْخَالِقُ|Al-Khaliq", "الْبَارِئُ|Al-Bari'",
    "الْمُصَوِّرُ|Al-Musawwir", "الْغَفَّارُ|Al-Ghaffar", "الْقَهَّارُ|Al-Qahhar", "الْوَهَّابُ|Al-Wahhab",
    "الرَّزَّاقُ|Ar-Razzaq", "الْفَتَّاحُ|Al-Fattah", "الْعَلِيمُ|Al-Aleem", "الْقَابِضُ|Al-Qabid",
    "الْبَاسِطُ|Al-Basit", "الْخَافِضُ|Al-Khafid", "الرَّافِعُ|Ar-Rafi'", "الْمُعِزُّ|Al-Mu'izz",
    "الْمُذِلُّ|Al-Mudhill", "السَّمِيعُ|As-Sami'", "الْبَصِيرُ|Al-Basir", "الْحَكَمُ|Al-Hakam",
    "الْعَدْلُ|Al-Adl", "اللَّطِيفُ|Al-Latif", "الْخَبِيرُ|Al-Khabir", "الْحَلِيمُ|Al-Haleem",
    "الْعَظِيمُ|Al-Azeem", "الْغَفُورُ|Al-Ghafoor", "الشَّكُورُ|Ash-Shakoor", "الْعَلِيُّ|Al-Ali",
    "الْكَبِيرُ|Al-Kabeer", "الْحَفِيظُ|Al-Hafeez", "الْمُقِيتُ|Al-Muqeet", "الْحَسِيبُ|Al-Haseeb",
    "الْجَلِيلُ|Al-Jaleel", "الْكَرِيمُ|Al-Kareem", "الرَّقِيبُ|Ar-Raqeeb", "الْمُجِيبُ|Al-Mujeeb",
    "الْوَاسِعُ|Al-Wasi'", "الْحَكِيمُ|Al-Hakeem", "الْوَدُودُ|Al-Wadood", "الْمَجِيدُ|Al-Majeed",
    "الْبَاعِثُ|Al-Ba'ith", "الشَّهِيدُ|Ash-Shaheed", "الْحَقُّ|Al-Haqq", "الْوَكِيلُ|Al-Wakeel",
    "الْقَوِيُّ|Al-Qawiyy", "الْمَتِينُ|Al-Mateen", "الْوَلِيُّ|Al-Waliyy", "الْحَمِيدُ|Al-Hameed",
    "الْمُحْصِي|Al-Muhsi", "الْمُبْدِئُ|Al-Mubdi'", "الْمُعِيدُ|Al-Mu'eed", "الْمُحْيِي|Al-Muhyi",
    "الْمُمِيتُ|Al-Mumeet", "الْحَيُّ|Al-Hayy", "الْقَيُّومُ|Al-Qayyoom", "الْوَاجِدُ|Al-Wajid",
    "الْمَاجِدُ|Al-Majid", "الْوَاحِدُ|Al-Wahid", "الْأَحَدُ|Al-Ahad", "الصَّمَدُ|As-Samad",
    "الْقَادِرُ|Al-Qadir", "الْمُقْتَدِرُ|Al-Muqtadir", "الْمُقَدِّمُ|Al-Muqaddim", "الْمُؤَخِّرُ|Al-Mu'akhkhir",
    "الْأَوَّلُ|Al-Awwal", "الْآخِرُ|Al-Akhir", "الظَّاهِرُ|Az-Zahir", "الْبَاطِنُ|Al-Batin",
    "الْوَالِي|Al-Wali", "الْمُتَعَالِي|Al-Muta'ali", "الْبَرُّ|Al-Barr", "التَّوَّابُ|At-Tawwab",
    "الْمُنْتَقِمُ|Al-Muntaqim", "الْعَفُوُّ|Al-Afuww", "الرَّءُوفُ|Ar-Ra'uf", "مَالِكُ الْمُلْكِ|Malik-ul-Mulk",
    "ذُو الْجَلَالِ وَالْإِكْرَامِ|Dhul-Jalali wal-Ikram", "الْمُقْسِطُ|Al-Muqsit", "الْجَامِعُ|Al-Jami'",
    "الْغَنِيُّ|Al-Ghani", "الْمُغْنِي|Al-Mughni", "الْمَانِعُ|Al-Mani'", "الضَّارُّ|Ad-Darr",
    "النَّافِعُ|An-Nafi'", "النُّورُ|An-Noor", "الْهَادِي|Al-Hadi", "الْبَدِيعُ|Al-Badee'",
    "الْبَاقِي|Al-Baqi", "الْوَارِثُ|Al-Warith", "الرَّشِيدُ|Ar-Rasheed", "الصَّبُورُ|As-Saboor"
)
