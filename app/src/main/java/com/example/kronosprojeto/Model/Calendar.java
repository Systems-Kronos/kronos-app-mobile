package com.example.kronosprojeto.model;

import java.util.Locale;

public class Calendar {

        // Será que da erro por estar em inglês?
        private String id;
        private Long user;
        private String day; // vem como String no JSON
        private Boolean presence;
        private String observation;
        private String crm;
        private String cid;
        private Boolean accepted;
        private String attest;

        public Calendar (String id, Long user, String day, Boolean presence, String observation, String crm, String cid, Boolean accepted, String attest  ){
                this.id = id;
                this.user = user;
                this.day = day;
                this.presence = presence;
                this.observation = observation;
                this.crm = crm;
                this.cid = cid;
                this.accepted = accepted;
                this.attest = attest;
        }
        public String getDay() { return day; }
        public Boolean getPresence() { return presence; }


}
