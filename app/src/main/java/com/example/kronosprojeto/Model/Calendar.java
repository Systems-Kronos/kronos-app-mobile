package com.example.kronosprojeto.model;

import com.google.gson.annotations.SerializedName;

public class Calendar {

        private String id;

        @SerializedName("usuario")
        private Long user;
        @SerializedName("idGestor")
        private Long manager;

        @SerializedName("evento")
        private String event;

        @SerializedName("presenca")
        private Boolean presence;

        @SerializedName("observacao")
        private String observation;

        @SerializedName("crm")
        private String crm;

        @SerializedName("cid")
        private String cid;

        @SerializedName("aceito")
        private Boolean accepted;

        @SerializedName("atestado")
        private String medicalCertificate;
        public Calendar() {
        }
        public Calendar(String id, Long user, String day, Boolean presence, String observation,
                        String crm, String cid, Boolean accepted, String attest) {
                this.id = id;
                this.user = user;
                this.event = day;
                this.presence = presence;
                this.observation = observation;
                this.crm = crm;
                this.cid = cid;
                this.accepted = accepted;
                this.medicalCertificate = attest;
        }

        public Long getManager() {
                return manager;
        }

        public void setManager(Long manager) {
                this.manager = manager;
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
                return event;
        }

        public void setDay(String day) {
                this.event = day;
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
                return medicalCertificate;
        }

        public void setAttest(String attest) {
                this.medicalCertificate = attest;
        }
        @Override
        public String toString() {
                return "Calendar{" +
                        "id='" + id + '\'' +
                        ", user=" + user +
                        ", manager=" + manager +
                        ", event='" + event + '\'' +
                        ", presence=" + presence +
                        ", observation='" + observation + '\'' +
                        ", crm='" + crm + '\'' +
                        ", cid='" + cid + '\'' +
                        ", accepted=" + accepted +
                        ", medicalCertificate='" + medicalCertificate + '\'' +
                        '}';
        }

}
