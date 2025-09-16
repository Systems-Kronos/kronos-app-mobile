package com.example.kronosprojeto.model;

public class Calendar {
        private String id;
        private Integer usuario;
        private String dia; // vem como String no JSON
        private Boolean presenca;
        private String observacao;
        private String crm;
        private String cid;
        private Boolean aceito;
        private String atestado;

        public String getDay() { return dia; }
        public Boolean getPresenca() { return presenca; }


}
