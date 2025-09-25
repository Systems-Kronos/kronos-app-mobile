package com.example.kronosprojeto.model;

public class Calendar {

        private String id;
        private Long user;
        private String day; // vem como String no JSON
        private Boolean presence;
        private String observation;
        private String crm;
        private String cid;
        private Boolean accepted;
        private String attest;

        public Calendar() {
        }
        public Calendar(String id, Long user, String day, Boolean presence, String observation,
                        String crm, String cid, Boolean accepted, String attest) {
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


        public String getId() {
                return id;
        }

        public void setId(String id) {
                this.id = id;
        }

        public Long getUser() {
                return user;
        }

        public void setUser(Long user) {
                this.user = user;
        }

        public String getDay() {
                return day;
        }

        public void setDay(String day) {
                this.day = day;
        }

        public Boolean getPresence() {
                return presence;
        }

        public void setPresence(Boolean presence) {
                this.presence = presence;
        }

        public String getObservation() {
                return observation;
        }

        public void setObservation(String observation) {
                this.observation = observation;
        }

        public String getCrm() {
                return crm;
        }

        public void setCrm(String crm) {
                this.crm = crm;
        }

        public String getCid() {
                return cid;
        }

        public void setCid(String cid) {
                this.cid = cid;
        }

        public Boolean getAccepted() {
                return accepted;
        }

        public void setAccepted(Boolean accepted) {
                this.accepted = accepted;
        }

        public String getAttest() {
                return attest;
        }

        public void setAttest(String attest) {
                this.attest = attest;
        }
}
