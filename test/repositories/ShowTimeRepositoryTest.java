package repositories;

public class ShowTimeRepositoryTest {

//    private final ShowTimeDao fakeShowTimeDao = new ShowTimeDao() {
//        @Override
//        public void insert(@NotNull ShowTime showTime) { }
//
//        @Override
//        public void update(@NotNull ShowTime showTime, @NotNull ShowTime copy) { }
//
//        @Override
//        public void delete(@NotNull ShowTime showTime) {
//
//        }
//
//        @Override
//        public List<ShowTime> get(@NotNull Movie movie) {
//            return showTimeRepo.getEntities().values().stream().filter(v ->
//                v.get() != null && Objects.requireNonNull(v.get()).getMovie().getId() == movie.getId()
//            ).map(Reference::get).toList();
//        }
//    };
//
//    private ShowTimeRepository showTimeRepo = ShowTimeRepositoryImpl.getInstance(fakeShowTimeDao);
//    private ShowTime testShowTime1;
//    private ShowTime testShowTime2;
//
//    @BeforeAll
//    public static void setUpAll(){
//        CinemaDatabaseTest.setUp();
//    }
//
//    @BeforeEach
//    public void setUpEach(){
//        testShowTime1 = CinemaDatabaseTest.getTestShowTime1();
//        testShowTime2 = CinemaDatabaseTest.getTestShowTime2();
//        showTimeRepo.getEntities().put(testShowTime1.getId(), new WeakReference<>(testShowTime1));
//        showTimeRepo.getEntities().put(testShowTime2.getId(), new WeakReference<>(testShowTime2));
//    }
//
//    @AfterAll
//    public static void tearDownAll(){
//        CinemaDatabaseTest.tearDown();
//    }
//
//
//    @Test
//    public void updateShowTime_success(){
//        assertDoesNotThrow(() ->
//                showTimeRepo.update(testShowTime1, (st) ->
//                        st.setMovie(CinemaDatabaseTest.getTestMovie2())
//                )
//        );
//        ShowTime cachedShowTime = showTimeRepo.getEntities().get(testShowTime1.getId()).get();
//        assertNotNull(cachedShowTime);
//        assertEquals(cachedShowTime.getMovie(), CinemaDatabaseTest.getTestMovie2());
//    }
//
//    @Test
//    public void getShowTime_success(){
//        List<ShowTime> showTimes = assertDoesNotThrow(() ->
//            showTimeRepo.get(CinemaDatabaseTest.getTestMovie1())
//        );
//        assertEquals(showTimes.getFirst(), testShowTime1);
//    }


}
