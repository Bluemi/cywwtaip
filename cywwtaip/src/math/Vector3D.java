package math;

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

    public float getX() { return this.xn[0]; }
    public float getY() { return this.xn[1]; }
    public float getZ() { return this.xn[2]; }

    public static float dotProduct(Vector3D a, Vector3D b) {
        float sum = 0.f;
        for (int i = 0; i < SIZE; i++)
            sum += a.xn[i] * b.xn[i];
        return sum;
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

    public static Vector3D fromTo(Vector3D from, Vector3D to) {
        return Vector3D.sub(to, from);
    }
}
