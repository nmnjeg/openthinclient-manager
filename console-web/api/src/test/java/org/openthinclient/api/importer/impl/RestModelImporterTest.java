package org.openthinclient.api.importer.impl;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openthinclient.api.importer.config.ImporterConfiguration;
import org.openthinclient.api.importer.model.ImportableHardwareType;
import org.openthinclient.api.importer.model.ProfileReference;
import org.openthinclient.api.importer.model.ProfileType;
import org.openthinclient.common.model.Device;
import org.openthinclient.common.model.HardwareType;
import org.openthinclient.common.model.schema.provider.SchemaProvider;
import org.openthinclient.common.model.service.ApplicationService;
import org.openthinclient.common.model.service.ClientService;
import org.openthinclient.common.model.service.DeviceService;
import org.openthinclient.common.model.service.HardwareTypeService;
import org.openthinclient.common.model.service.LocationService;
import org.openthinclient.common.model.service.PrinterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.mockito.BDDMockito.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;


@RunWith(SpringRunner.class)
@Import({ImporterConfiguration.class, RestModelImporterTest.ClasspathSchemaProviderConfiguration.class})
public class RestModelImporterTest {

  @Configuration
  public static class ClasspathSchemaProviderConfiguration {
    @Bean
    public SchemaProvider schemaProvider() {
      return new ClasspathSchemaProvider();
    }
  }

  private RestModelImporter importer;

  @MockBean
  HardwareTypeService hardwareTypeService;
  @MockBean
  ApplicationService applicationService;
  @MockBean
  ClientService clientService;
  @MockBean
  DeviceService deviceService;
  @MockBean
  LocationService locationService;
  @MockBean
  PrinterService printerService;

  @Autowired
  ImportModelMapper mapper;

  @Before
  public void setUp() throws Exception {

    importer = new RestModelImporter(mapper, hardwareTypeService, applicationService, clientService, deviceService, locationService, printerService);
  }

  @Test
  public void testImportSimpleHardwareType() throws Exception {

    final ImportableHardwareType hw = new ImportableHardwareType();
    hw.setName("Simple Hardware Type");

    final HardwareType hardwareType = importer.importHardwareType(hw);

    assertNotNull(hardwareType);

    then(hardwareTypeService).should().save(any());

  }

  @Test(expected = MissingReferencedObjectException.class)
  public void testImportHardwareTypeWithMissingDevice() throws Exception {
    final ImportableHardwareType hw = new ImportableHardwareType();
    hw.setName("Incomplete Hardware Type");
    hw.getDevices().add(new ProfileReference(ProfileType.DEVICE, "Missing Device"));

    importer.importHardwareType(hw);

  }

  @Test
  public void testImportHardwareTypeWithReferencedDevice() throws Exception {

    final Device existingDevice = new Device();
    existingDevice.setName("RequiredDevice");
    given(deviceService.findByName("Required Device")).willReturn(existingDevice);

    final ImportableHardwareType hw = new ImportableHardwareType();
    hw.setName("Complete Hardware Type");
    hw.getDevices().add(new ProfileReference(ProfileType.DEVICE, "Required Device"));

    final HardwareType result = importer.importHardwareType(hw);

    assertNotNull(result);
    assertSame(existingDevice, result.getDevices().iterator().next());

    then(hardwareTypeService).should().save(any());

  }
}