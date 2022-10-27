![simple-lettuce-rpc](https://thehackernews.com/new-images/img/b/R29vZ2xl/AVvXsEh09KugWf9Nll7KSG7yZBNIvMLXvLKZ92heAygg8X6PYa2oq5Gp7OARqFBSZyMbfZCsrcK9Mh72AhpOgxuEXhmjAynK6iRSEf_xMMAl_T0oqulTMyMrJgAc7PDPFVO0MuKFWRJessc_Iu5-Rm-QSXVXRVTrU_666K232IVvIKEiChh39TVtKy5BnyQY/w0/redis.jpg)

---

# simple-lettuce-rpc

A simple RPC framework based on Lettuce for Java applications.

### 💻 Maven

#### Step 1. Add the JitPack repository to your pom.xml

```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>
```

#### Step 2. Add the dependency to your pom.xml

```xml
<dependency>
    <groupId>com.github.xcodiq</groupId>
    <artifactId>simple-lettuce-rpc</artifactId>
    <version>VERSION</version>
</dependency>
```

#### Step 3. Change the version to the latest release version

*Put latest version thing here*

### 📖 Example

In this example I'm implementing the RPC principle by letting a Client application request a Server application for
specific data based on the provided record data.

#### Step 1. Create a new Record, I recommend creating a separate class existing Record

```java
public final class TestRecord extends Record<BytePacket, IntegerPacket> {
	
	public TestRecord(int input) {
		super(new BytePacket((byte) input));
	}
}
```

#### Step 2. Create a RecordHandler for the record, either anonymously or by a separate class

```java
public class TestRecordHandler extends RecordHandler<BytePacket, IntegerPacket> {

	@Override
	public IntegerPacket handlePacket(BytePacket packet) {
		return new IntegerPacket(packet.getPayload() == 1 
				? Server.DATA_INTEGER_TRUE : Server.DATA_INTEGER_FALSE);
	}
}
```

#### Step 3. Create a new Server instance and bind the RecordHandler to the created Record

```java
import com.xcodiq.rpc.Options;

public final class Server {

	// data to share with clients
	public static final int DATA_INTEGER_TRUE = 1337;
	public static final int DATA_INTEGER_FALSE = 42069;

	public Server() {
		// Create a new RPC instance
		final RPC<Server> rpc = new RPC<>(Server.this, Options.of("REDIS_URI", "TOPIC", "RECORD_PREFIX"));

		// Get the record manager, and bind the record handler to the created record
		final RecordManager recordManager = rpc.getRecordManager();
		recordManager.bindRecordHandler(TestRecord.class, new TestRecordHandler());
	}
}
```

#### Step 4. Create a new Client instance and request the data from the Server

```java
public final class Client {

	public Client() {
		// Create a new RPC instance
		final RPC<Client> rpc = new RPC<>(Client.this, Options.of("REDIS_URI", "TOPIC", "RECORD_PREFIX"));

		// I used a completable future to make sure the data is received before the program continues
		CompletableFuture<Integer> dataFuture = new CompletableFuture<>();

		// Create the new record, and configure the timeout and reply consumers
		final Record<BytePacket, IntegerPacket> testRecord = new TestRecord(0) //<- Input is 0 so Output Should be 42069
				.onTimeout(bytePacket -> dataFuture.complete(-1))
				.onReply(integerPacket -> dataFuture.complete(integerPacket.getPayload()));

		// Set the timeout interval and publish it
		testRecord.setTimeout(500, TimeUnit.MILLISECONDS).send(rpc.getRecordManager());

		// When the data is received, print it
		dataFuture.whenComplete((integer, throwable) -> System.out.println("Received data: " + integer));
	}
}
```

### 📝 License

This project is licensed under the MIT License
