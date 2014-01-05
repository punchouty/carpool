package com.racloop;

import java.util.ArrayList;

import org.elasticsearch.common.geo.GeoPoint;

/**
 * Use http://www.birdtheme.org/useful/v3tool.html to generate city data
 * 
 * @author rpunch
 * 
 */
public class CityUtil {

	GeoPoint[] chennai = { new GeoPoint(80.323792, 13.390956),
			new GeoPoint(79.869232, 13.394963),
			new GeoPoint(79.832153, 12.926938),
			new GeoPoint(79.981842, 12.755553),
			new GeoPoint(80.226288, 12.770286),
			new GeoPoint(80.279846, 13.062088),
			new GeoPoint(80.307312, 13.197165),
			new GeoPoint(80.343018, 13.297421),
			new GeoPoint(80.323792, 13.390956) };
	private static final ArrayList<GeoPoint> city1 = new ArrayList<GeoPoint>();
	private static final ArrayList<GeoPoint> city2 = new ArrayList<GeoPoint>();
	
	static {
		city1.add(new GeoPoint(80.323792,13.390956));
		city1.add(new GeoPoint(79.869232,13.394963));
		city1.add(new GeoPoint(79.832153,12.926938));
		city1.add(new GeoPoint(79.981842,12.755553));
		city1.add(new GeoPoint(80.226288,12.770286));
		city1.add(new GeoPoint(80.279846,13.062088));
		city1.add(new GeoPoint(80.307312,13.197165));
		city1.add(new GeoPoint(80.343018,13.297421));

		city2.add(new GeoPoint(80.343018,13.297421));
		city2.add(new GeoPoint(80.307312,13.197165));
		city2.add(new GeoPoint(80.279846,13.062088));
		city2.add(new GeoPoint(80.226288,12.770286));
		city2.add(new GeoPoint(79.981842,12.755553));
		city2.add(new GeoPoint(79.832153,12.926938));
		city2.add(new GeoPoint(79.869232,13.394963));
		city2.add(new GeoPoint(80.323792, 13.390956));
	}

	/**
	 * Array represent close polygon - first and last geopoints are same Uses
	 * "Winding Number" algorithm Notes: - This is a very simple algorithm that
	 * compares the number of downward vectors with the number of upward vectors
	 * surrounding a specified point. - This algorithm was designed for a 2D
	 * plane and will fail for a curved surface where the distance between
	 * points is great. Observations: - It appears that the state borders may
	 * have been defined by simple X/Y cooridinated based on latitude/longitude
	 * values. The simple cases are states bordered by constant longitudes or
	 * latitudes.
	 * 
	 * @param boundary
	 * @return
	 */
	public static boolean isPointInside(ArrayList<GeoPoint> boundary,
			GeoPoint pointToTest) {
		boundary = closePloygon(boundary);
		int windingNumber = 0; // the winding number counter
		for (int i = 0; i < boundary.size() - 1; i++) { // edge from V[i] to
														// V[i+1]
			if (boundary.get(i).lat() <= pointToTest.lat()) { // start y <= P.y
				System.out.println("y <= P.y");
				if (boundary.get(i + 1).lat() > pointToTest.lat()) { // an upward crossing
					if (isLeft(boundary.get(i), boundary.get(i + 1), pointToTest) > 0.0) { // P left of edge
						++windingNumber; // have a valid up intersect
					}
				}
			} else {
				System.out.println("y > P.y");
				if (boundary.get(i + 1).lat() <= pointToTest.lat()) { // a downward crossing
					if (isLeft(boundary.get(i), boundary.get(i + 1), pointToTest) < 0.0) { // P right of edge
						--windingNumber; // have a valid down intersect
					}
				}
			}
		}
		System.out.println("windingNumber : " + windingNumber);
		return (windingNumber == 0) ? false : true; // windingNumber == 0 if
													// point is OUTSIDE
	}

	/**
	 * Tests if the point, <code>pointToTest</code>, is Left|On|Right of an infinite line
	 * formed by <code>geoPoint1</code> and <code>geoPoint2</code>
	 * 
	 * @param geoPoint1
	 * @param geoPoint2
	 * @param pointToTest
	 * @return <ul>
	 *        <li> >0 for pointToTest left of the line through geoPoint1 and geoPoint2</li>
	 *         <li> =0 for pointToTest on the line</li>
 	 *         <li> <0 for pointToTest right of the line</li>
 	 *        </ul>
	 */
	private static double isLeft(GeoPoint geoPoint1, GeoPoint geoPoint2, GeoPoint pointToTest) {
		double val = (geoPoint2.lon() - geoPoint1.lon()) * (pointToTest.lat() - geoPoint1.lat())- 
				(pointToTest.lon() - geoPoint1.lon()) * (geoPoint2.lat() - geoPoint1.lat());
		
		System.out.println("isLeft : " + val);
		return val;
	}

	private static ArrayList<GeoPoint> closePloygon(ArrayList<GeoPoint> boundary) {
		if (boundary.size() < 3)
			return null;
		else {
			GeoPoint first = boundary.get(0);
			GeoPoint last = boundary.get(boundary.size() - 1);
			if (first.equals(last)) {
				// already closed
				return boundary;
			} else {
				// close and return new array
				ArrayList<GeoPoint> newBoundary = new ArrayList<GeoPoint>();
				for (GeoPoint geoPoint : boundary) {
					newBoundary.add(geoPoint);
				}
				System.out.println("Closing Polygon");
				newBoundary.add(new GeoPoint(first.lat(), first.lon()));
				return newBoundary;
			}
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		GeoPoint pointToTestOutside = new GeoPoint(13.456408,79.831939);
		GeoPoint pointToTestInside = new GeoPoint(13.044697,80.198607);
		boolean result = isPointInside(city1, pointToTestOutside);
		System.out.println(result);
		result = isPointInside(city1, pointToTestInside);
		System.out.println(result);
		result = isPointInside(city2, pointToTestOutside);
		System.out.println(result);
		result = isPointInside(city2, pointToTestInside);
		System.out.println(result);
	}

}
