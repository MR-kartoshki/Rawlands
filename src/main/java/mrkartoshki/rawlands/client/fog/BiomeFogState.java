package mrkartoshki.rawlands.client.fog;

public final class BiomeFogState {
    private final float transitionRate;
    private float target;
    private float strength;

    public BiomeFogState(float transitionRate) {
        if (!Float.isFinite(transitionRate) || transitionRate <= 0.0f || transitionRate > 1.0f) {
            throw new IllegalArgumentException("transitionRate must be in (0, 1]");
        }
        this.transitionRate = transitionRate;
    }

    public void setTarget(float target) {
        if (!Float.isFinite(target)) {
            throw new IllegalArgumentException("target must be finite");
        }
        this.target = Math.clamp(target, 0.0f, 1.0f);
    }

    public float update(float target) {
        setTarget(target);
        return tick();
    }

    public float tick() {
        strength += (target - strength) * transitionRate;
        return strength;
    }

    public float strength() {
        return strength;
    }
}
