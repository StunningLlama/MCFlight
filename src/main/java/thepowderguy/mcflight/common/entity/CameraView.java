package thepowderguy.mcflight.common.entity;

public class CameraView {
	public float prevZoom;
	public float zoom;
	public double viewYawOffset;
	public double viewPitchOffset;
	public final String name;
	public CameraView(String namein) {
		name = namein;
		zoom = 7f;
		prevZoom = zoom;
		viewYawOffset = 0;
		viewPitchOffset = 0;
	}
}