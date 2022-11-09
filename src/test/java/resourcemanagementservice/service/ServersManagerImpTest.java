package resourcemanagementservice.service;

import com.allocator.resourcemanagementservice.exception.RefusedRequestException;
import com.allocator.resourcemanagementservice.exception.ServerNotFoundException;
import com.allocator.resourcemanagementservice.service.Server;
import com.allocator.resourcemanagementservice.service.ServersManagerImp;
import com.allocator.resourcemanagementservice.service.State;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.opentest4j.AssertionFailedError;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.AdditionalMatchers.lt;
import static org.mockito.ArgumentMatchers.anyFloat;

@ExtendWith(MockitoExtension.class)
class ServersManagerImpTest {

  @BeforeAll
  static void setUp() throws NoSuchFieldException, IllegalAccessException {
    Server server1 = Mockito.mock(Server.class);
    Mockito.when(server1.getId()).thenReturn(1L);
    Mockito.when(server1.isAvailable(lt(Server.SERVER_SIZE/4 + 1))).thenReturn(true);
    Mockito.when(server1.isActive()).thenReturn(true);

    Server server2 = Mockito.mock(Server.class);
    Mockito.when(server2.getId()).thenReturn(2L);
    Mockito.when(server2.isAvailable(anyFloat())).thenReturn(false);
    Mockito.when(server2.isActive()).thenReturn(true);

    injectServersIntoServersManager(server1, server2);
  }

  @Test
  void getInstance_Singleton_ReturnSameInstance(){
    ServersManagerImp serversManagerImp = ServersManagerImp.getINSTANCE();
    assertEquals(serversManagerImp,
        ServersManagerImp.getINSTANCE(),
        "getInstance should return same singleton instance");
  }

  @Test
  void getServerById_ServerExists_True(){
    assertDoesNotThrow(() ->
        assertNotNull(ServersManagerImp.getINSTANCE().getServer(1),
            "Failed to retrieve an existing server"));
  }

  @Test
  void getServerById_ServerDoesNotExists_ThrowsServerNotFoundException(){
    assertThrowsExactly(ServerNotFoundException.class,() ->
        ServersManagerImp.getINSTANCE().getServer(350),
        "Should throw exception as the server doesn't exist");
  }

  @Test
  void getServers_MoreThanTwoServers_True(){
    assertDoesNotThrow(() ->
            assertTrue(ServersManagerImp.getINSTANCE().getServers().size() >= 2,
                "Incorrect number of servers retrieved"),
        "Exception thrown although there are existing servers");
  }

  //This test is for practicing java reflection to get and test private methods
  //But it's not a good practice to test private methods
  @Test
  void findFreeServer_NoAvailableServer_ReturnsNull()
      throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
    assertNull(searchInExistingServersMethod(Server.SERVER_SIZE));
  }

  //This test is for practicing java reflection to get and test private methods
  //But it's not a good practice to test private methods
  @Test
  void findFreeServer_AvailableServer_ReturnsServer()
      throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
    assertEquals(1,
        searchInExistingServersMethod(Server.SERVER_SIZE/4).getId());
  }

  @Test
  void allocateServer_FinishExecutionAfterStartupDelay_True(){
    //This nested assertions is the only way I thought of to assert that the method don't terminate before the server delay
    assertThrowsExactly(AssertionFailedError.class, ()->
        assertTimeout(Duration.ofMillis(Server.SERVER_STARTUP_DELAY), ()->
            ServersManagerImp.getINSTANCE().allocateServer(Server.SERVER_SIZE)),
        "Service responded before activation of server, it should wait the server startup delay.");
  }

  @Test
  void allocateServer_ServerIsInActiveState_True()
      throws InterruptedException, RefusedRequestException {
    Server newServer = ServersManagerImp.getINSTANCE().allocateServer(Server.SERVER_SIZE);
    assertEquals(State.ACTIVE, newServer.getState(), "Server isn't in active state yet");
  }

  @Test
  void allocateServer_AvailableFreeServer_AllocateToTheFreeServer()
      throws InterruptedException, RefusedRequestException {
    Server newServer = ServersManagerImp.getINSTANCE().allocateServer(Server.SERVER_SIZE/4);
    assertEquals(1, newServer.getId(), "Server isn't in active state yet");
  }

  @Test
  void allocateServer_SimultaneousRequestsToTheSameServer_True()
      throws InterruptedException, RefusedRequestException {
    Server Server1 = ServersManagerImp.getINSTANCE().allocateServer(Server.SERVER_SIZE/3);
    Server Server2 = ServersManagerImp.getINSTANCE().allocateServer(Server.SERVER_SIZE/3);
    Thread.sleep(1000);
    Server Server3 = ServersManagerImp.getINSTANCE().allocateServer(Server.SERVER_SIZE/3);
    assertTrue(Server1.getId() == Server2.getId() &&
        Server2.getId() == Server3.getId(),
        "Failed to assigned simultaneous requests correctly");
  }

  @Test
  void allocateServer_MoreThanMaximumSize_ThrowRefusedRequestException(){
    assertThrowsExactly(RefusedRequestException.class, () ->
        ServersManagerImp.getINSTANCE().allocateServer(Server.SERVER_SIZE*2),
        "Should throw exception as user can't allocate memory more than the Server's maximum size.");
  }

  //Java reflection is used to get this private method from inside ServersManager class
  //The string used inside the method getDeclaredMethod is the name of the private method required
  private Server searchInExistingServersMethod(float memorySize)
      throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
    Method method = ServersManagerImp.class.getDeclaredMethod("findFreeServer", float.class);
    method.setAccessible(true);
    return (Server) method.invoke(ServersManagerImp.getINSTANCE(), memorySize);
  }

  //Java reflection is used to edit the private servers attribute without exposing it with a public getters and setters
  private static void injectServersIntoServersManager(Server... newServers)
      throws IllegalAccessException, NoSuchFieldException {
    Field serversField = ServersManagerImp.class.getDeclaredField("servers");
    serversField.setAccessible(true);
    Object serversObject = serversField.get(ServersManagerImp.getINSTANCE());
    try{
      if(serversObject instanceof ArrayList<?>){
        ArrayList<?> ManagerServers = ((ArrayList<?>) serversObject);
        if(ManagerServers.isEmpty() || ManagerServers.get(0) instanceof Server) {
          ((ArrayList<Server>) ManagerServers)
              .addAll(Arrays.asList(newServers));
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
