FileManagemt - z tej klasy będą wykonywane operacje na pilkach
Jeśli metoda wykonała się poprawnie to zwraca true, w innym wypadku false.
W przypadku czytania metody zwracają zczytany ciag znaków w postaci Stringa, w przypadku błędu metoda zwraca null.

public static boolean displayFCB(String fileName); //wyświetla FCB pliku o podanej nazwie, czyli
// nazwę pliku, nazwę użytkownika który utworzył plik, indeks w tablicy FAT, rozmiar danych pliku w bajtach i wartość semafora

public static void printFileSystem(); //wyświetla zawartść tablicy FAT, wektor bitowy i zawartość dysku

public static boolean signalFile(String file_name); // podnosi semafor na pliku o podanej nazwie

public static boolean waitFile(String file_name, int PID); //opuszcza semafor pliku o podanej nazwie, utworzonego przez proces
//o podanym PID

public static boolean printSem(String file_name); //wyswietla wartość semafora pliku o podanej nazwie

public static boolean create(String name, String user); //tworzenie pliku o podanej nazwie, przez użytkownika o nazwie user

public static boolean write(String name, String data); //zapis/dopisanie do pliku o nazwie name danych data, zapis danych na dysku

public static String read(String fileName, int from, int howMany); //zczytywanie określonej liczby bajtów (nazwa pliku z którego zczytujemy,
// od którego bajtu zaczynamy zczytywanie, ile bajtów zczytujemy), zwraca String zawierający zczytane bajty

public static String readFile(String fileName); //zczytanie danych z pliku o podanej nazwie, zwraca zawartość pliku

public static boolean delete(String name); //usuwa plik o podanej nazwie, usuwa jego dane z dysku oraz jego FCB i usuwa z katalogu
