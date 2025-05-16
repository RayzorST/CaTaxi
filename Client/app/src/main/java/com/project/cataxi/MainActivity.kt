package com.project.cataxi

import android.content.Intent
import android.graphics.PointF
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusState
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import com.project.cataxi.database.ApiClient
import com.project.cataxi.database.orders.OrdersPostRequest
import com.project.cataxi.database.orders.OrdersResponse
import com.project.cataxi.datastore.SearchHistoryDataStore
import com.project.cataxi.datastore.SearchHistoryViewModelFactory
import com.project.cataxi.datastore.SearchViewModel
import com.project.cataxi.datastore.SettingsDataStore
import com.project.cataxi.datastore.ThemeViewModel
import com.project.cataxi.datastore.ThemeViewModelFactory
import com.project.cataxi.datastore.UserDataStore
import com.project.cataxi.datastore.UserViewModel
import com.project.cataxi.datastore.UserViewModelFactory
import com.project.cataxi.ui.theme.CaTaxiTheme
import com.yandex.mapkit.Animation
import com.yandex.mapkit.GeoObjectCollection
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.map.IconStyle
import com.yandex.mapkit.map.VisibleRegionUtils
import com.yandex.mapkit.mapview.MapView
import com.yandex.mapkit.search.Response
import com.yandex.mapkit.search.SearchFactory
import com.yandex.mapkit.search.SearchManager
import com.yandex.mapkit.search.SearchManagerType
import com.yandex.mapkit.search.SearchOptions
import com.yandex.mapkit.search.SearchType
import com.yandex.mapkit.search.Session
import com.yandex.runtime.Error
import com.yandex.runtime.image.ImageProvider
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback


class MainActivity : ComponentActivity(), Session.SearchListener {
    private lateinit var mapView: MapView
    private lateinit var searchManager: SearchManager
    private var searchSession: Session? = null

    private object placeholderObject {
        var state  = mutableStateOf(PlaceholderState.NONE)
        var address = mutableListOf(GeoObjectCollection.Item())
        lateinit var addressHistory: State<List<SearchHistoryDataStore.SearchHistoryItem>>
    }

    private object address {
        val focusOn = mutableStateOf(0)
        val addressA = mutableStateOf("")
        val addressB = mutableStateOf("")
    }

    private object user {
        var firstName = mutableStateOf("")
        var secondName = mutableStateOf("")
        val email = mutableStateOf("")
    }

    @RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        MapKitFactory.initialize(this)

        val settingsDataStore = SettingsDataStore(this)
        val settingsViewModelFactory = ThemeViewModelFactory(settingsDataStore)

        val searchHistoryDataStore = SearchHistoryDataStore(this)
        val searchHistoryViewModelFactory = SearchHistoryViewModelFactory(searchHistoryDataStore)

        val userDataStore = UserDataStore(this)
        val userViewModelFactory = UserViewModelFactory(userDataStore)

        mapView = MapView(this)

        setContent {
            val themeViewModel: ThemeViewModel = viewModel(factory = settingsViewModelFactory)
            val isDarkTheme by themeViewModel.isDarkTheme.collectAsState(initial = false)

            val searchViewModel: SearchViewModel = viewModel(factory = searchHistoryViewModelFactory)
            placeholderObject.addressHistory = searchViewModel.searchHistory.collectAsState()

            val userViewModel: UserViewModel = viewModel(factory = userViewModelFactory)
            user.email.value = userViewModel.email.collectAsState(initial = "").value

            mapView.map.isNightModeEnabled = isDarkTheme

            CaTaxiTheme (darkTheme = isDarkTheme, dynamicColor = false) {
                MapScreen(searchViewModel)
            }
        }

        searchManager = SearchFactory.getInstance().createSearchManager(SearchManagerType.COMBINED);

    }

    override fun onStart() {
        mapView.onStart();
        MapKitFactory.getInstance().onStart();
        super.onStart()
    }

    override fun onStop() {
        mapView.onStop();
        MapKitFactory.getInstance().onStop();
        super.onStop()
    }

    override fun onSearchResponse(response: Response) {
        if (response.collection.children.isEmpty()){
            placeholderObject.state.value = PlaceholderState.NOTFOUND
        }
        else {
            placeholderObject.state.value = PlaceholderState.SUCCESS
        }
        placeholderObject.address = response.collection.children
    }

    override fun onSearchError(p0: Error) {
        placeholderObject.state.value = PlaceholderState.ERROR
    }

    private fun submitQuery(query: String) {
        searchSession = searchManager.submit(
            query,
            VisibleRegionUtils.toPolygon(mapView.mapWindow.map.visibleRegion),
            SearchOptions().apply {
                searchTypes = SearchType.GEO.value
                resultPageSize = 10
            },
            this
        )
    }

    @RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
    @Composable
    fun MapScreen(searchViewModel: SearchViewModel) {
        Box(modifier = Modifier.fillMaxSize()) {
            MapViewContainer(modifier = Modifier.fillMaxSize())

            AvatarButton(
                modifier = Modifier
                    .padding(20.dp)
                    .align(Alignment.TopStart)
            )


            Column (Modifier
                .align(Alignment.BottomCenter),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                when (placeholderObject.state.value) {
                    PlaceholderState.NOTFOUND -> {
                        Placeholder(Modifier,
                            onClickButton = {
                                placeholderObject.state.value = PlaceholderState.NOTFOUND
                            },
                            placeholder = "404",
                            description = "Ничего не получилось найти",
                            searchViewModel = searchViewModel)
                    }
                    PlaceholderState.ERROR -> {
                        Placeholder(Modifier,
                            existButton = true,
                            onClickButton = {
                                placeholderObject.state.value = PlaceholderState.ERROR
                            },
                            placeholder = "Ошибка при поиске",
                            description = "Попробуйте чуть позже",
                            searchViewModel = searchViewModel)
                    }

                    PlaceholderState.SUCCESS -> {
                        Placeholder(Modifier,
                            onClickButton = { },
                            placeholder = "Вот, что получилось найти",
                            searchViewModel = searchViewModel)
                    }

                    PlaceholderState.SEARCH -> {
                        Placeholder(Modifier,
                            onClickButton = {},
                            placeholder = "Ищем...",
                            searchViewModel = searchViewModel)
                    }

                    PlaceholderState.HISTORY -> {
                        if (placeholderObject.addressHistory.value.isNotEmpty()){
                            Placeholder(Modifier,
                                existButton = true,
                                buttonName = "Удалить историю",
                                onClickButton = { searchViewModel.clearHistory()  },
                                placeholder = "Что искали ранее",
                                searchViewModel = searchViewModel)
                        }
                    }

                    PlaceholderState.NONE -> {}
                }

                Spacer(Modifier.size(5.dp))

                BottomMenu(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight(unbounded = true)
                )
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
    @Composable
    fun Placeholder(
        modifier: Modifier = Modifier,
        placeholder: String,
        searchViewModel: SearchViewModel,
        description: String = "",
        existButton: Boolean = false,
        buttonName: String = "Обновить",
        onClickButton: () -> Unit = {}) {
        Box(modifier = modifier
            .clip(shape = RoundedCornerShape(16.dp))
            .fillMaxWidth(0.95f)
            .wrapContentHeight(unbounded = true)
            .background(MaterialTheme.colorScheme.background)
            .border(1.dp, Color.Black, shape = RoundedCornerShape(16.dp))
        )
        {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = placeholder,
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.primary,
                    textAlign = TextAlign.Center
                )
                if (placeholderObject.state.value != PlaceholderState.SUCCESS && placeholderObject.state.value != PlaceholderState.HISTORY){

                    if (description.isNotEmpty()) {
                        Text(
                            text = description,
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.onBackground,
                            textAlign = TextAlign.Center
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    if (placeholderObject.state.value == PlaceholderState.SEARCH){
                        LinearProgressIndicator(
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    Row() {
                        Button(
                            modifier = Modifier.padding(8.dp),
                            onClick = {
                                placeholderObject.state.value = PlaceholderState.NONE
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                        ) {
                            Text("Закрыть")
                        }
                    }
                }
                if (placeholderObject.state.value == PlaceholderState.HISTORY){
                    for (item in placeholderObject.addressHistory.value){
                        AddressButton(
                            onClick = {
                                mapView.map.move(
                                    CameraPosition(item.point!!, 15.0f, 0.0f, 0.0f),
                                    Animation(Animation.Type.SMOOTH, 1f),
                                    null
                                )
                                mapView.mapWindow.map.mapObjects.addPlacemark().apply {
                                    geometry = item.point
                                    setText("ffefe")
                                }

                                searchViewModel.addToHistory(item.query, item.point)
                                placeholderObject.state.value = PlaceholderState.NONE
                                if (address.focusOn.value == 0){
                                    address.addressA.value = item.query
                                }
                                else{
                                    address.addressB.value = item.query
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .wrapContentHeight(unbounded = false)
                                .padding(vertical = 0.dp, horizontal = 0.dp),
                            addressObj = item.query
                        )
                    }
                }
                if (placeholderObject.state.value == PlaceholderState.SUCCESS){
                    for (item in placeholderObject.address){
                        AddressButton(
                            onClick = {
                                mapView.map.move(
                                    CameraPosition(item.obj?.geometry!![0].point!!, 15.0f, 0.0f, 0.0f),
                                    Animation(Animation.Type.SMOOTH, 1f),
                                    null
                                )
                                mapView.mapWindow.map.mapObjects.addPlacemark().apply {
                                    geometry = item.obj?.geometry!![0].point!!
                                    setText("ffefe")
                                }

                                searchViewModel.addToHistory(item.obj?.name.toString(), item.obj?.geometry!![0].point!!)
                                placeholderObject.state.value = PlaceholderState.NONE
                                if (address.focusOn.value == 0){
                                    address.addressA.value = item.obj?.name.toString()
                                }
                                else{
                                    address.addressB.value = item.obj?.name.toString()
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .wrapContentHeight(unbounded = false)
                                .padding(vertical = 0.dp, horizontal = 0.dp),
                            addressObj = item.obj?.name.toString()
                        )
                    }
                }
                if (existButton) {
                    Button(
                        modifier = Modifier.padding(8.dp),
                        onClick = onClickButton,
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Text(buttonName)
                    }
                }
            }
        }
    }

    @Composable
    fun AddressButton(
        modifier: Modifier,
        onClick: () -> Unit,
        addressObj: String
    ) {
        Box(modifier = modifier
            .clickable(onClick = onClick)
            .background(MaterialTheme.colorScheme.background)
            .padding(2.dp)) {
            Text(
                text = addressObj,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                softWrap = false,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier
                    .align(Alignment.CenterStart)
            )
        }
    }

    @Composable
    fun MapViewContainer(modifier: Modifier = Modifier) {
        val point = remember { mutableStateOf(Point(55.751574, 37.573856)) }
        val zoom = remember { mutableFloatStateOf(11f) }
        val azimuth = remember { mutableFloatStateOf(0f) }
        val tilt = remember { mutableFloatStateOf(0f) }

        AndroidView(
            factory = { _ ->
               mapView.apply {
                   map.move(
                       CameraPosition(
                           point.value,
                           zoom.floatValue,
                           azimuth.floatValue,
                           tilt.floatValue
                       )
                   )
               }
            },
            modifier = modifier
        )
    }

    @Composable
    fun AvatarButton(modifier: Modifier = Modifier) {
        Box(
            modifier = modifier
                .size(48.dp)
                .clip(CircleShape)
                .border(1.dp, Color.Black, shape = CircleShape)
                .background(MaterialTheme.colorScheme.primary)
                .clickable {
                    startActivity(Intent(this@MainActivity, AccountActivity::class.java))
                },
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.account),
                contentDescription = "Аватарка пользователя",
                modifier = Modifier.fillMaxSize()
            )
        }
    }

    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    fun BottomMenu(modifier: Modifier = Modifier) {
        val context = LocalContext.current
        val scope = rememberCoroutineScope()
        val taxiTypes = listOf(
            "Микроавтобус" to "Вместимость: до 1,5 тонн или 10-15 м³",
            "Газель" to "Вместимость: до 2 тонн или 16-20 м³",
            "Бортовая Газель" to "Вместимость: до 3 тонн или 20-25 м³",
            "Рефрижератор" to "Вместимость: до 1,5 тонн или 10-15 м³",
            "Грузовик (5-10 тонн)" to "Вместимость: до 10 тонн или 40-50 м³",
            "Фургон (до 20 тонн)" to "Вместимость: до 20 тонн или 80-100 м³"
        )
        val pagerState = rememberPagerState(pageCount = {
            taxiTypes.size
        })


        Surface(
            modifier = modifier
                .border(1.dp, Color.Black, shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)),
            color = MaterialTheme.colorScheme.background,
            shadowElevation = 8.dp,
            shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceAround
            ) {
                Text(text = "CaTaxi", color = MaterialTheme.colorScheme.primary)

                SearchBar("Точка А", address.addressA,
                    {
                        address.addressA.value = it
                        scope.launch {
                            while (address.addressA.value.isNotEmpty() && address.addressA.value == it) {
                                placeholderObject.state.value = PlaceholderState.SEARCH
                                delay(1000)
                                submitQuery(it)
                                delay(2000)
                            }
                        }
                    },
                    {
                        address.focusOn.value = 0
                    })

                SearchBar("Точка Б", address.addressB,
                    {
                        address.addressB.value = it
                        scope.launch {
                            while (address.addressB.value.isNotEmpty() && address.addressB.value == it) {
                                placeholderObject.state.value = PlaceholderState.SEARCH
                                delay(1000)
                                submitQuery(it)
                                delay(2000)
                            }
                        }
                    },
                    {
                        address.focusOn.value = 1
                    })

                TaxiCardPager(pagerState, taxiTypes)

                Row(modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)){
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clickable { },
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.card),
                            contentDescription = "Карта оплаты",
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(5.dp)
                        )
                    }

                    Button(
                        onClick = {
                            if (address.addressA.value.isNotEmpty() and address.addressB.value.isNotEmpty()){
                                val call = ApiClient.ordersApi.post(OrdersPostRequest(
                                    taxiTypes[pagerState.currentPage].first,
                                    address.addressA.value,
                                    address.addressB.value,
                                    user.email.value)
                                )

                                call.enqueue(object: Callback<OrdersResponse> {
                                    override fun onResponse(call: Call<OrdersResponse>, response: retrofit2.Response<OrdersResponse>
                                    ) {
                                        if (response.isSuccessful){
                                            Toast.makeText(
                                                context,
                                                "Заказ создан",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                        else{
                                            Toast.makeText(
                                                context,
                                                "Ошибка",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    }

                                    override fun onFailure(call: Call<OrdersResponse>, t: Throwable) {
                                        Toast.makeText(
                                            context,
                                            "Ошибка сети: ${t.message}",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                })
                                Toast.makeText(
                                    context,
                                    "Заказ создан",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                            else{
                                Toast.makeText(
                                    context,
                                    "Не заполнены адреса/ов",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        },
                        modifier = Modifier
                            .height(56.dp)
                            .fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Text("Заказать", color = Color.White)
                    }
                }
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun SearchBar(
        hint: String,
        searchText: MutableState<String>,
        onValueChange: (String) -> Unit,
        onFocusChange: (FocusState) -> Unit,
        modifier: Modifier = Modifier
    ) {
        val keyboardController = LocalSoftwareKeyboardController.current

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp)
                .onFocusChanged(onFocusChange)
        ) {
            OutlinedTextField(
                value = searchText.value,
                onValueChange = onValueChange,
                modifier = modifier
                    .weight(1f)
                    .onFocusChanged {
                        focusState ->
                        if (focusState.isFocused) {
                            placeholderObject.state.value = PlaceholderState.HISTORY
                        }
                        else{
                            placeholderObject.state.value = PlaceholderState.NONE
                        }
                    },
                label = { Text(text = hint) },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(
                    onDone = { keyboardController?.hide() }
                ),
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedLabelColor = MaterialTheme.colorScheme.primary,
                    focusedBorderColor = MaterialTheme.colorScheme.primary
                )
            )

            if (searchText.value.isNotEmpty()) {
                IconButton(
                    modifier = Modifier.padding(top = 8.dp),
                    onClick = {
                        searchText.value = ""
                        keyboardController?.hide()
                        placeholderObject.state.value = PlaceholderState.NONE
                    }
                ) {
                    Icon(imageVector = Icons.Default.Clear, contentDescription = "Очистить")
                }
            }
        }
    }

    @Composable
    fun TaxiCard(title: String, description: String) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            colors = CardDefaults.cardColors(MaterialTheme.colorScheme.primary),
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    text = title,
                    fontSize = 20.sp,
                    color = MaterialTheme.colorScheme.onPrimary
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = description,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    }

    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    fun TaxiCardPager(pagerState: PagerState, taxiTypes: List<Pair<String, String>>) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .fillMaxWidth()
            ) { page ->
                TaxiCard(
                    title = taxiTypes[page].first,
                    description = taxiTypes[page].second
                )
            }
        }
    }
}

enum class PlaceholderState {
    SUCCESS,
    NONE,
    NOTFOUND,
    SEARCH,
    ERROR,
    HISTORY
}