"""
Autor : Groapa Mihai
Grupa : 334 CA

This module represents a device.

Computer Systems Architecture Course
Assignment 1
March 2017
"""

from threading import Event, Thread, Lock
from reusable_barrier import ReusableBarrier

def find_all_locations(number_of_devices, devices):
    """
    Lista locatiilor obtinuta din concatenarea listei locatiilor
    specifice fiecarui dispozitiv.

    @type: number_of_devices : Integer
    @param: number_of_devices : Numar dispozitive
    @type: devices : List of Devices
    @param: devices : Lista tuturor dispozitivelor

    @rtype: List of Integers
    @return: Lista cu toate locatiile vizitate de toate dispozitivele,
    incluzand si valori duplicate
    """
    locations = []

    for i in range(number_of_devices):
        locations += devices[i].get_locations_list()

    return locations

class Device(object):
    """
    Class that represents a device.
    """
    # pylint: disable=too-many-instance-attributes
    def __init__(self, device_id, sensor_data, supervisor):
        """
        Constructor.

        @type device_id: Integer
        @param device_id: the unique id of this node; between 0 and N-1

        @type sensor_data: List of (Integer, Float)
        @param sensor_data: a list containing (location, data) as measured by this device

        @type supervisor: Supervisor
        @param supervisor: the testing infrastructure's control and validation component
        """
        self.device_id = device_id
        self.sensor_data = sensor_data
        self.supervisor = supervisor
        self.script_received = Event()
        self.scripts = []
        self.timepoint_done = Event()
        self.threads = []
        self.number_of_threads = 8
        self.neighbours = []
        self.devices_barrier = None
        self.threads_barrier = ReusableBarrier(self, self.number_of_threads, True)
        self.number_of_locations = 0
        self.locations_locks = []
        self.number_of_devices = 0
        self.devices_list = []

    def set_neighbours(self, neighbours):
        """
        Setter pentru lista de vecini

        @type neighbours : List of Devices
        @param neighbours : Lista vecinilor
        """
        self.neighbours = neighbours

    def set_devices_barrier(self, devices_barrier):
        """
        Setter pentru bariera dintre dispozitive

        @type devices_barrier : ReusableBarrier
        @param devices_barrier : bariera dintre dispozitive
        """
        self.devices_barrier = devices_barrier

    def set_number_of_locations(self, number_of_locations):
        """
        Setter pentru numarul de locatii in care a fost cel putin un device

        @type number_of_locations : Integer
        @param number_of_locations : numar locatii vizitate
        """
        self.number_of_locations = number_of_locations

    def set_locations_locks(self, locations_locks):
        """
        Setter pentru lista de locks, unul per locatie

        @type number_of_locations : List of Locks
        @param number_of_locations : lock-urile de acces privilegiat la locatii
        """
        self.locations_locks = locations_locks

    def set_number_of_devices(self, number_of_devices):
        """
        Setter pentru numarul total de dispozitive

        @type number_of_devices : Integer
        @param number_of_devices : numarul total de dispozitive
        """
        self.number_of_devices = number_of_devices

    def has_neighbours(self):
        """
        Verifica daca un dispozitiv are cel putin un vecin.

        @rtype: Boolean
        @return: True daca are cel putin un vecin
        """
        if len(self.neighbours) != 0:
            return True
        return False

    def get_locations_list(self):
        """
        Obtine lista cu locatiile vizitate de un device.

        @rtype: List of Integers
        @return: Lista locatiilor vizitate
        """
        locations_list = []

        for location in self.sensor_data:
            locations_list.append(location)
        return locations_list

    def __str__(self):
        """
        Pretty prints this device.

        @rtype: String
        @return: a string containing the id of this device
        """
        return "Device %d" % self.device_id

    def setup_devices(self, devices):
        """
        Setup the devices before simulation begins.

        @type devices: List of Device
        @param devices: list containing all devices
        """
        number_of_devices = len(devices)
        self.devices_list = devices
        self.set_number_of_devices(number_of_devices)

        # doar device-ul Master face setarile initiale
        if self.device_id == 0:
            locations_locks = []

            # creaza bariera partajata de device-uri
            barrier = ReusableBarrier(None, number_of_devices, False)

            # e suficient sa calculeze indexul maxim al locatiilor pentru
            # a sti cate lock-uri sa creeze si sa partajeze cu celelalte
            # device-uri pt fiecare locatie
            locations = find_all_locations(number_of_devices, devices)
            number_of_locations = 1 + max(locations)

            # creaza un lock pentru fiecare locatie
            for i in range(number_of_locations):
                locations_locks.append(Lock())

            # partajeaza cu celelalte device-uri bariera, lista de lock-uri
            # si numarul de locatii
            for i in range(number_of_devices):
                devices[i].set_devices_barrier(barrier)
                devices[i].set_locations_locks(locations_locks)
                devices[i].set_number_of_locations(number_of_locations)

        # fiecare device creeaza si porneste cele "number_of_threads"
        # thread-uri detinute
        for i in range(self.number_of_threads):
            self.threads.append(DeviceThread(i, self))
            self.threads[-1].start()

    def assign_script(self, script, location):
        """
        Provide a script for the device to execute.

        @type script: Script
        @param script: the script to execute from now on at each timepoint;
        None if the current timepoint has ended

        @type location: Integer
        @param location: the location for which the script is interested in
        """
        if script is not None:
            self.scripts.append((script, location))
            self.script_received.set()
        else:
            self.timepoint_done.set()

    def get_data(self, location):
        """
        Returns the pollution value this device has for the given location.

        @type location: Integer
        @param location: a location for which obtain the data

        @rtype: Float
        @return: the pollution value
        """
        return self.sensor_data[location] if location in self.sensor_data \
            else None

    def set_data(self, location, data):
        """
        Sets the pollution value stored by this device for the given location.

        @type location: Integer
        @param location: a location for which to set the data

        @type data: Float
        @param data: the pollution value
        """
        if location in self.sensor_data:
            self.sensor_data[location] = data

    def shutdown(self):
        """
        Instructs the device to shutdown (terminate all threads). This method
        is invoked by the tester. This method must block until all the threads
        started by this device terminate.
        """
        for i in range(self.number_of_threads):
            self.threads[i].join()

class DeviceThread(Thread):
    """
    Class that implements the device's worker thread.
    """
    def __init__(self, thread_id, device):
        """
        Constructor.

        @type device: Device
        @param device: the device which owns this thread
        """
        Thread.__init__(self, name="Device Thread %d" % device.device_id)
        self.thread_id = thread_id
        self.device = device
        self.script_data = []

    def gather_data(self, data):
        """
         Aduna data de la device pentru locatia curenta si adauga la lista

         @type data: Float
         @param data: valoarea nivelului de poluare masurata de un device
         """
        if data is not None:
            self.script_data.append(data)

    def update_neighbours_data(self, script, location):
        """
         Ruleaza script-ul cu datele acumulate de la vecini despre locatia
         curenta. Obtine valoarea imbunatatita despre nivelul de zgomot si
         actualizeaza informatia pentru dispozitivele care au contribuit.

         @type script: Script
         @param script: scriptul de rulat
         @type location: Integer
         @param location: locatia la care se actualizeaza datele
         """
        updated_data = script.run(self.script_data)

        # actualizeaza informatiile vecinilor
        for device in self.device.neighbours:
            device.set_data(location, updated_data)

        # actualizeaza nivelul de zgomot propriu al zonei
        self.device.set_data(location, updated_data)

    def run(self):
        while True:
            pos = self.thread_id

            # bariera inter - thread-uri folosita pentru
            # a apela get_neighbours() o singura data
            self.device.threads_barrier.wait()

            # obtine vecinii
            if self.device.neighbours is None:
                break

            # bariera inter - device-uri folosita pentru a nu
            # dezechilibra timepoint-ul la care se afla thread-urile
            self.device.timepoint_done.wait()

            # fiecare thread executa scripturi dupa urmatoarea regula:
            # thread-ul i : script[i], script[i + num_threads], ...
            # script[i + k * num_threads] in functie de totalul scripturilor
            if self.device.has_neighbours():
                while pos < len(self.device.scripts):
                    self.script_data = []
                    (script, location) = self.device.scripts[pos]
                    pos += self.device.number_of_threads

                    # acces privilegiat la o locatie data intre devices
                    with self.device.locations_locks[location]:
                        data = self.device.get_data(location)
                        self.gather_data(data)

                        # adun date de la dispozitive vecine
                        for device in self.device.neighbours:
                            if location in device.sensor_data:
                                data = device.get_data(location)
                                self.gather_data(data)

                        # daca am date pentru a putea rula scriptul, fac
                        # acest lucru si actualizez nivelul de poluare
                        if self.script_data != []:
                            self.update_neighbours_data(script, location)
