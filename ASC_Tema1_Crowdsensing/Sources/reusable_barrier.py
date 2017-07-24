""""
Autor : Groapa Mihai
Grupa : 334 CA

Bariera reentranta, folosita in 2 scopuri:
sincronizare devices, si threads, la nivelul unui device
"""

from threading import Condition

class ReusableBarrier(object):
    """
    Clasa bariera reentranta
    """
    def __init__(self, device, num_objects, flag):
        """
        Constructor.

        @type device: Device
        @param device: the device which owns this thread
        @type flag: Boolean
        @param flag: True pentru bariera la nivel
        de dispozitive, False altfel
        """
        self.device = device
        self.num_objects = num_objects
        self.flag = flag
        self.count_objects = self.num_objects
        self.cond = Condition()

    # folosesc Flag pentru a face posbila scrierea intr-o singura clasa a
    # celor 2 tipuri de bariere pe care le folosesc: cea la nivel de
    # thread-uri ale unui dispozitiv si cea inter-dispozitive, care asigura
    # echilibrul intre timepoints
    def wait(self):
        """"
        Bariera reentranta cu Condition
        """
        with self.cond:
            self.count_objects -= 1
            if self.count_objects == 0:
                if self.flag:
                    self.device.devices_barrier.wait()
                    self.device.timepoint_done.clear()
                    self.device.set_neighbours(self.device.supervisor.get_neighbours())
                self.count_objects = self.num_objects
                self.cond.notify_all()
            else:
                self.cond.wait()

    def __str__(self):
        return "Barrier for %d objects" % self.num_objects

    def set_num_objects(self, new_num_objects):
        """setter pentru numarul de obiecte"""
        self.num_objects = new_num_objects

    def reset_barrier(self):
        """ resetare bariera """
        self.cond.notify_all()
        self.count_objects = self.num_objects
