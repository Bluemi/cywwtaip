package math;

import java.util.Random;

public class Vector3D {
    private final float[] xn;
    public static final int SIZE = 3;

    public Vector3D() {
        this.xn = new float[SIZE];
        for (int i = 0; i < SIZE; i++)
            this.xn[i] = 0.f;
    }

    public Vector3D(float[] xn) {
        if (xn.length != SIZE)
            throw new IllegalArgumentException("Given argument has invalid size");

        this.xn = new float[SIZE];
        System.arraycopy(xn, 0, this.xn, 0, SIZE);
    }

    public Vector3D(float x, float y, float z) {
        this.xn = new float[SIZE];
        this.xn[0] = x;
        this.xn[1] = y;
        this.xn[2] = z;
    }

    /**
     * @return a random normalized vector
     */
    public static Vector3D getRandomNormalized() {
        Random random = new Random(System.currentTimeMillis());
        return new Vector3D(random.nextFloat()*2.f - 1.f, random.nextFloat()*2.f - 1.f, random.nextFloat()*2.f - 1.f).normalized();
    }

    /**
     * @return the positive angle between a and b in radians. Returns Nan, if one of a or b has length 0.
     */
    public static float getAngleBetween(Vector3D a, Vector3D b) {
        float dotProd = Vector3D.dotProduct(a, b);
        float sub = a.getLength() * b.getLength();
        float normDotProduct = dotProd / sub;

        if (Math.abs(normDotProduct) > 1.f)
            normDotProduct = Math.signum(normDotProduct);

        return (float) Math.acos(normDotProduct);
    }

    public float getX() { return this.xn[0]; }
    public float getY() { return this.xn[1]; }
    public float getZ() { return this.xn[2]; }
    public float get(int index) { return this.xn[index]; }

    public Vector3D set(float v, int index) {
        Vector3D result = new Vector3D();
        result.xn[index] = v;
        return result;
    }

    public static float dotProduct(Vector3D a, Vector3D b) {
        float sum = 0.f;
        for (int i = 0; i < SIZE; i++)
            sum += a.xn[i] * b.xn[i];

        if (sum > 1.f)
            sum = 1.f;
        return sum;
    }

    public static Vector3D crossProduct(Vector3D a, Vector3D b) {
        return new Vector3D(
            a.xn[1] * b.xn[2] - a.xn[2] * b.xn[1],
            a.xn[2] * b.xn[0] - a.xn[0] * b.xn[2],
            a.xn[0] * b.xn[1] - a.xn[1] * b.xn[0]
        );
    }

    public static boolean isAngleAcute(Vector3D a, Vector3D b) {
        return dotProduct(a, b) > 0.f;
    }

    public static boolean isAngleObtuse(Vector3D a, Vector3D b) {
        return dotProduct(a, b) < 0.f;
    }

    public static boolean isOrthogonal(Vector3D a, Vector3D b) {
        return dotProduct(a, b) == 0.f;
    }

    public static Vector3D add(Vector3D a, Vector3D b) {
        Vector3D result = new Vector3D();
        for (int i = 0; i < SIZE; i++)
            result.xn[i] = a.xn[i] + b.xn[i];
        return result;
    }

    public static Vector3D sub(Vector3D a, Vector3D b) {
        Vector3D result = new Vector3D();
        for (int i = 0; i < SIZE; i++)
            result.xn[i] = a.xn[i] - b.xn[i];
        return result;
    }

    public Vector3D scale(float f) {
        Vector3D result = new Vector3D();
        for (int i = 0; i < SIZE; i++)
            result.xn[i] = this.xn[i] * f;
        return result;
    }

    public Vector3D withLength(float f) {
        float length = getLength();
        if (length == 0.f)
            return new Vector3D();

        return this.scale(f / length);
    }

    public static Vector3D fromTo(Vector3D from, Vector3D to) {
        return Vector3D.sub(to, from);
    }

    public Vector3D normalized() {
        float length = getLength();
        if (length == 0.f)
            return new Vector3D();
        return this.scale(1.f / length);
    }

    public float getLengthSquared() {
        float lengthSquared = 0.f;
        for (int i = 0; i < SIZE; i++) {
            lengthSquared += this.xn[i] * this.xn[i];
        }
        return lengthSquared;
    }

    public float getLength() {
        return (float) Math.sqrt(getLengthSquared());
    }

    public static float getDistanceBetween(Vector3D a, Vector3D b) {
        Vector3D fromTo = Vector3D.sub(a, b);
        return fromTo.getLength();
    }

    public static float getDistanceSquaredBetween(Vector3D a, Vector3D b) {
        Vector3D fromTo = Vector3D.sub(a, b);
        return fromTo.getLengthSquared();
    }

    /**
     * Projects this vector on to projectionVector
     * @param projectionVector The vector to project this vector on to
     * @return This vector projected on to projectionVector
     */
    public Vector3D projectOn(Vector3D projectionVector) {
        Vector3D projectionVectorNormalized = projectionVector.normalized();
        return projectionVectorNormalized.scale(dotProduct(this, projectionVectorNormalized));
    }

    /**
     * @return the index of the coordinate with the highest absolute value. If two coordinates have the same absolute
     * value, the first of these coordinate indices is returned.
     */
    public int absArgMax() {
        float highest = Math.abs(this.xn[0]);
        int index = 0;

        for (int i = 1; i < SIZE; i++) {
            float v = Math.abs(this.xn[i]);
            if (v > highest) {
                highest = v;
                index = i;
            }
        }

        return index;
    }

    public float absMax() {
        float highest = this.xn[0];

        for (int i = 1; i < SIZE; i++) {
            float v = Math.abs(this.xn[i]);
            if (v > Math.abs(highest)) {
                highest = v;
            }
        }

        return highest;
    }

    @Override
    public String toString() {
        return "(" + this.xn[0] + ", " + this.xn[1] + ", " + this.xn[2] + ')';
    }

    public boolean isZero() {
        for (int i = 0; i < SIZE; i++) {
            if (this.xn[i] != 0.f)
                return false;
        }
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Vector3D) {
            Vector3D other = (Vector3D)o;
            for (int i = 0; i < 3; i++) {
                if (this.xn[i] != other.xn[i]) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }
}
