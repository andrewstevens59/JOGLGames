package Vortex;

public class Vector3f 
{
	public float x, y, z;
 
	// Constructors as well as getters/setters omitted for brevity!!
	// Only important methods kept necessary for this tutorial.
	// The original class contains many more methods...
 
	public Vector3f add(Vector3f a) {
		x += a.x;
		y += a.y;
		z += a.z;
 
		return this;
	}
 
	public Vector3f set(Vector3f v)	{
		this.x = v.x;
		this.y = v.y;
		this.z = v.z;
 
		return this;
	}
 
	public Vector3f subAndAssign(Vector3f a, Vector3f b) {
		x = a.x - b.x;
		y = a.y - b.y;
		z = a.z - b.z;
 
		return this;
	}
 
	/**
	 * Returns the length of the vector, also called L2-Norm or Euclidean Norm.
	 */
	public float l2Norm() {
		return (float) Math.sqrt(x*x+y*y+z*z);
	}
 
	public Vector3f crossAndAssign(Vector3f a, Vector3f b) {
		float tempX = a.y * b.z - a.z * b.y;
		float tempY = a.z * b.x - a.x * b.z;
		float tempZ = a.x * b.y - a.y * b.x;
 
		x = tempX;
		y = tempY;
		z = tempZ;
 
		return this;
	}
 
	public Vector3f scale(float scalar) {
		x *= scalar;
		y *= scalar;
		z *= scalar;
 
		return this;
	}
 
	public Vector3f normalize() {
		float length = l2Norm();
		x /= length;
		y /= length;
		z /= length;
 
		return this;
	}
}