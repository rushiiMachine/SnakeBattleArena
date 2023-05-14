package apcs.snakebattlearena.commands;

public abstract class Command<T> {
    public abstract String getId();

    public abstract T toJsonData();
}
