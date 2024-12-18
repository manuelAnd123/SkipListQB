class MyEntry {
    private Integer key;
    private String value;
    public MyEntry(Integer key, String value) {
        this.key = key;
        this.value = value;
    }
    public Integer getKey() {
        return key;
    }
    public String getValue() {
        return value;
    }
    @Override
    public String toString() {
        return key + " " + value;
    }
}
