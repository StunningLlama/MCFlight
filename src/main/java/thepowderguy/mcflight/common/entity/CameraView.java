package thepowderguy.mcflight.common.entity;

public class CameraView {
	public float prevZoom;
	public float zoom;
	public double viewYawOffset;
	public double viewPitchOffset;
	public final String name;
	public final boolean renderPlayer;
	public CameraView(String namein, boolean doRenderPlayer) {
		name = namein;
		zoom = 7f;
		prevZoom = zoom;
		viewYawOffset = 0;
		viewPitchOffset = 0;
		renderPlayer = doRenderPlayer;
	}
}