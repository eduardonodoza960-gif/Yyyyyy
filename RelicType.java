package com.imdd.reliquias.registry;

public enum RelicType {
    VELOCIDADE("velocidade"),
    FURTIVIDADE("furtividade"),
    LENHADOR("lenhador"),
    MINERACAO("mineracao"),
    ARQUEIRO("arqueiro"),
    SORTE("sorte"),
    GUERRA("guerra"),
    DEFESA("defesa"),
    FIM("fim"),
    INVASOR("invasor"),
    IMORTALIDADE("imortalidade"),
    OCEANO("oceano"),
    EXPLORADOR("explorador"),
    ESPIAO("espiao"),
    DEUS_DO_VENTO("deus_do_vento"),
    WARDEN("warden"),
    STOMPER("stomper"),
    DRAGAO("dragao"),
    INVISIBILIDADE("invisibilidade"),
    NETHER("nether");

    private final String id;

    RelicType(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public static RelicType fromId(String id) {
        for (RelicType type : values()) {
            if (type.id.equals(id)) return type;
        }
        return null;
    }
}
