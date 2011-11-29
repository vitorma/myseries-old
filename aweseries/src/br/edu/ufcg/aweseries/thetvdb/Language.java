package br.edu.ufcg.aweseries.thetvdb;

public enum Language {
    DA,
    FI,
    NL,
    DE,
    IT,
    ES,
    FR,
    PL,
    HU,
    EL,
    TR,
    RU,
    HE,
    JA,
    PT,
    ZH,
    CS,
    SL,
    HR,
    KO,
    EN,
    SV,
    NO;

    public static Language from(String language) {
        return valueOf(language.toUpperCase());
    }
}

