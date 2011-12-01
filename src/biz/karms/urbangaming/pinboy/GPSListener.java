package biz.karms.urbangaming.pinboy;

import javax.microedition.location.Criteria;
import javax.microedition.location.Location;
import javax.microedition.location.LocationException;
import javax.microedition.location.LocationListener;
import javax.microedition.location.LocationProvider;
import javax.microedition.location.QualifiedCoordinates;

/**
 * GPSListener class
 * Listens to the LocationProvider...
 * @author Karm
 */
public class GPSListener implements LocationListener {

    private LocationProvider locationProvider = null;
    private int gpsDataRetrieveInterval = Constants.GPS_DATA_RETRIEVE_INTERVAL;
    //private int updatedTimes = 0;
    
    /**
     * Default GPS position value.
     */
    private QualifiedCoordinates coords = null;
    private Criteria criteria = new Criteria();
    
    /**
     * Just an empty constructor.
     */
    public GPSListener() {
    }

    /**
     * Listens whether the location was or was not updated.
     *
     * @param provider
     * @param location
     */
    public void locationUpdated(LocationProvider provider, Location location) {
    	//updatedTimes++;
        if (location != null && location.isValid()) {
            coords = location.getQualifiedCoordinates();
        }
    }

    public void providerStateChanged(LocationProvider provider, int newState) {
    	//Nothing
    }

    /**
     * Connect to the GPS device.
     * @throws LocationException
     */
    public void startGPS() {
        try {
        	if(locationProvider == null) {
        		criteria.setAddressInfoRequired(false);
        		criteria.setAltitudeRequired(false);
        		criteria.setCostAllowed(true);
        		criteria.setHorizontalAccuracy(Constants.HORIZONTAL_ACCURACY);
        		criteria.setPreferredPowerConsumption(Criteria.NO_REQUIREMENT);
        		criteria.setPreferredResponseTime(Constants.PREFERRED_RESPONSE_TIME);
        		criteria.setSpeedAndCourseRequired(false);
        		//criteria.setVerticalAccuracy(Criteria.NO_REQUIREMENT);
        		locationProvider = LocationProvider.getInstance(criteria);
        	}
            locationProvider.setLocationListener(this, gpsDataRetrieveInterval, Constants.GPS_TIME_OUT, -1);
        } catch (LocationException ex) {
            ex.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void stopGPS() {
    	if(locationProvider != null) {
    		locationProvider.setLocationListener(null, -1, -1, -1);
    	}
    }
    
    public int getState() {
    	if(locationProvider != null) {
    		return locationProvider.getState();
    	}
    	return Constants.GPS_LISTENER_IS_NULL;
    }

    public double getLatitude() {
    	if(coords != null)  {
    		return coords.getLatitude();
    	}
    	return Constants.GPS_LATI_LONG_DEFAULT;
    }

    public double getLongitude() {
    	if(coords != null)  {
    		return coords.getLongitude();
    	}
    	return Constants.GPS_LATI_LONG_DEFAULT;
    }

    /*public int getUpdatedTimes() {
		return updatedTimes;
	}*/

	/**
     * It is necessary to stop and start the GPS again if you 
     * want to change the GPS data retrieval interval.
     * @param gpsDataRetrieveInterval
     */
	public void setGpsDataRetrieveInterval(int gpsDataRetrieveInterval) {
		this.gpsDataRetrieveInterval = gpsDataRetrieveInterval;
	}    
}
