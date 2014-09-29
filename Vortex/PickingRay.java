package Vortex;

public class PickingRay 
{
	private Vector3f clickPosInWorld = new Vector3f();
	private Vector3f direction = new Vector3f();
 
	/**
	 * Computes the intersection of this ray with the X-Y Plane (where Z = 0)
	 * and writes it back to the provided vector.
	 */
	public void intersectionWithXyPlane(float[] worldPos)
	{
		float s = -clickPosInWorld.z / direction.z;
		worldPos[0] = clickPosInWorld.x+direction.x*s;
		worldPos[1] = clickPosInWorld.y+direction.y*s;
		worldPos[2] = 0;
	}
 
	public Vector3f getClickPosInWorld() {
		return clickPosInWorld;
	}
	public Vector3f getDirection() {
		return direction;
	}	
}
