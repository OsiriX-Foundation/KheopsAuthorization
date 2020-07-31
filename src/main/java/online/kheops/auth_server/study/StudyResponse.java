package online.kheops.auth_server.study;

import online.kheops.auth_server.entity.Instances;
import online.kheops.auth_server.entity.Series;
import online.kheops.auth_server.entity.Study;
import online.kheops.auth_server.series.SeriesResponse;

import javax.xml.bind.annotation.XmlElement;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class StudyResponse {

    @XmlElement(name = "patient_name")
    private String patientName;
    @XmlElement(name = "patient_ID")
    private String patientID;
    @XmlElement(name = "study_uid")
    private String studyInstanceUID;
    @XmlElement(name = "study_date")
    private String studyDate;
    @XmlElement(name = "study_time")
    private String studyTime;
    @XmlElement(name = "timezone_offset_from_utc")
    private String timezoneOffsetFromUtc;
    @XmlElement(name = "study_description")
    private String studyDescription;
    @XmlElement(name = "study_id")
    private String studyId;
    @XmlElement(name = "accession_number")
    private String accessionNumber;
    @XmlElement(name = "referring_physician_name")
    private String referringPhysicianName;
    @XmlElement(name = "patient_birth_date")
    private String patientBirthDate;
    @XmlElement(name = "patient_sex")
    private String patientSex;

    @XmlElement(name = "retrieve_url")
    private String retrieveUrl;

    @XmlElement(name = "series")
    private List<SeriesResponse> series;

    private String kheopsInstance = null;

    private StudyResponse() { /*empty*/ }

    public StudyResponse(Study study, boolean uidOnly, String kheopsInstance) {
        studyInstanceUID = study.getStudyInstanceUID();
        if (uidOnly) { return; }
        patientName = study.getPatientName();
        studyDate = study.getStudyDate();
        studyDescription = study.getStudyDescription();
        patientID = study.getPatientID();
        patientBirthDate = study.getPatientBirthDate();
        patientSex = study.getPatientSex();
        referringPhysicianName = study.getReferringPhysicianName();
        accessionNumber = study.getAccessionNumber();
        studyId = study.getStudyID();
        timezoneOffsetFromUtc = study.getTimezoneOffsetFromUTC();
        studyTime = study.getStudyTime();
        if(kheopsInstance != null) {
            this.kheopsInstance = kheopsInstance;
            retrieveUrl = kheopsInstance + "/api/studies/" + studyInstanceUID;
        }
    }

    public void addSeries(Series series) {
        if(this.series == null) {
            this.series = new ArrayList<>();
        }
        final SeriesResponse seriesResponse = new SeriesResponse(series, true, kheopsInstance);
        series.getInstances().forEach(instances -> seriesResponse.addInstances(instances.getInstanceUID()));
        this.series.add(seriesResponse);
    }

    public void addSeriesWithInstances(Series series, Set<Instances> instancesSet) {
        if(this.series == null) {
            this.series = new ArrayList<>();
        }
        final SeriesResponse seriesResponse = new SeriesResponse(series, false, kheopsInstance);
        instancesSet.forEach(instances -> seriesResponse.addInstances(instances.getInstanceUID()));
        this.series.add(seriesResponse);
    }

    public boolean containSeries() {
        if (series == null) {
            return false;
        } else {
            return !series.isEmpty();
        }
    }
}
