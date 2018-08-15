package shabado.payload;

import com.google.common.base.Objects;

public class TestObj {

    String testString;
    int testInt;

    TestObj(String testString, int testInt) {
        this.testString = testString;
        this.testInt = testInt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TestObj testObj = (TestObj) o;
        return testInt == testObj.testInt &&
                Objects.equal(testString, testObj.testString);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(testString, testInt);
    }
}
