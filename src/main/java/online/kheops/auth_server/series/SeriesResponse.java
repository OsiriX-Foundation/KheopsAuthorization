package online.kheops.auth_server.series;

import online.kheops.auth_server.entity.Series;

import javax.xml.bind.annotation.XmlElement;
import java.util.HashSet;
import java.util.Set;

public class SeriesResponse {

    @XmlElement(name = "modality")
    private String modality;
    @XmlElement(name = "series_description")
    private String seriesDescription;
    @XmlElement(name = "series_uid")
    private String seriesUid;
    @XmlElement(name = "number_of_series_related_instances")
    private Long numberOfSeriesRelatedInstance;
    @XmlElement(name = "time_zone_offset_from_utc")
    private String timeZoneOffsetFromUTC;
    @XmlElement(name = "series_number")
    private Long seriesNumber;
    @XmlElement(name = "body_part_examined")
    private String bodyPartExamined;
    @XmlElement(name = "retrieve_url")
    private String retrieveUrl;


    private SeriesResponse() { /*empty*/ }

    public SeriesResponse(Series series, boolean uidOnly, String kheopsInstance) {
        seriesUid = series.getSeriesInstanceUID();
        if (uidOnly) { return; }
        modality = series.getModality();
        numberOfSeriesRelatedInstance = Long.valueOf(series.getNumberOfSeriesRelatedInstances());
        seriesDescription = series.getSeriesDescription();
        timeZoneOffsetFromUTC = series.getTimezoneOffsetFromUTC();
        seriesNumber = Long.valueOf(series.getSeriesNumber());
        bodyPartExamined = series.getBodyPartExamined();
        if (kheopsInstance != null) {
            retrieveUrl = kheopsInstance + "/api/studies/" + series.getStudy().getStudyInstanceUID() + "/series/" + series.getSeriesInstanceUID();
        }
    }


    public void hideRetrieveUrl() {
        retrieveUrl = null;
    }
}
