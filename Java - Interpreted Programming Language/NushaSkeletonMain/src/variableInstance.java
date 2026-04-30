public class variableInstance {



String[] definition;
int currentValue;
boolean unique;


public variableInstance(String[] definition, int currentValue, boolean unique) {
    this.definition = definition;
    this.currentValue = currentValue;
    this.unique = unique;

}

public String getDefinition() {

    return definition[currentValue];
}
public int getCurrentValue() {
	return currentValue;

}
public boolean isUnique() {

    return unique;
}
}
