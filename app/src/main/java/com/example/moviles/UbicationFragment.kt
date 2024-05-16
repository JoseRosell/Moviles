package com.example.bnmenu.fragments

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.moviles.databinding.FragmentUbicationBinding
import org.osmdroid.api.IMapController
import org.osmdroid.config.Configuration.getInstance
import org.osmdroid.views.MapView
import org.osmdroid.util.GeoPoint
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.views.CustomZoomButtonsController
import org.osmdroid.views.CustomZoomButtonsDisplay
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [FragmentMaps.newInstance] factory method to
 * create an instance of this fragment.
 */
class UbicationFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private val REQUEST_PERMISSIONS_REQUEST_CODE = 1
    private lateinit var map : MapView
    private lateinit var mapController: IMapController
    private lateinit var binding: FragmentUbicationBinding

    // Inicializar el launcher para solicitar permisos de ubicación
    private val requestLocationPermissionLauncher: ActivityResultLauncher<String> = registerForActivityResult(
        ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
        if (isGranted)
        {
            // Permiso concedido, puedes realizar acciones relacionadas con la ubicación
            /* El permiso de localización ya está concedido */
            cargarMapa()

        }
        else
        {
            // Permiso denegado, muestra un mensaje de que la funcionalidad de ubicación no está disponible
            Toast.makeText(requireContext(), "El permiso no se ha concedido", Toast.LENGTH_LONG).show()
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.fragment_maps, container, false)
        binding = FragmentUbicationBinding.inflate(inflater, container, false)
        return binding.getRoot()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?)
    {
        super.onViewCreated(view, savedInstanceState)

        // Solicitar el permiso de ubicación si aún no se ha concedido
        if ((ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            || (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED))
        {
            requestLocationPermission()
        }
        else
        {
            // Permiso ya concedido, puedes realizar acciones relacionadas con la ubicación
            cargarMapa()
        }

    }

    // Función para solicitar permiso de ubicación
    private fun requestLocationPermission() {
        // Solicitar permiso de ubicación
        requestLocationPermissionLauncher.launch(Manifest.permission.ACCESS_COARSE_LOCATION)
        requestLocationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
    }

    private fun cargarMapa()
    {

        //load/initialize the osmdroid configuration, this can be done
        // This won't work unless you have imported this: org.osmdroid.config.Configuration.*
        getInstance().load(requireContext(), androidx.preference.
        PreferenceManager.getDefaultSharedPreferences(requireContext()))
        //setting this before the layout is inflated is a good idea
        //it 'should' ensure that the map has a writable location for the map cache, even without permissions
        //if no tiles are displayed, you can try overriding the cache path using Configuration.getInstance().setCachePath
        //see also StorageUtils
        //note, the load method also sets the HTTP User Agent to your application's package name, if you abuse osm's
        //tile servers will get you banned based on this string.

        //inflate and create the map --> No necesario en Fragment
        //setContentView(R.layout.activity_osmactivity)

        map = binding.map

        map.setTileSource(TileSourceFactory.MAPNIK)

        // Habilitar la interacción de zoom
        map.setBuiltInZoomControls(true)
        map.setMultiTouchControls(false)
        map.zoomController.setVisibility(CustomZoomButtonsController.Visibility.ALWAYS);
        val zoomButtonsDisplay = CustomZoomButtonsDisplay(map)
        zoomButtonsDisplay.setPositions(true,
            CustomZoomButtonsDisplay.HorizontalPosition.CENTER,CustomZoomButtonsDisplay.VerticalPosition.BOTTOM)

        /* Para situar el visor del mapa en un punto (Latitud,Longitud)*/
        mapController = map.controller

        //Cuanto mayor sea, más grande se verá el punto elegido
        mapController.setZoom(12.0)

        //Servigroup Marina Playa
        val startPoint = GeoPoint(37,130532, -3,583732);
        mapController.setCenter(startPoint);

//        /* MARCADORES */
//        val firstMarker = Marker(map)
//
//        var geoPoint = GeoPoint(37.15628, -1.825713)
//        firstMarker.position = geoPoint
//        firstMarker.icon = ContextCompat.getDrawable(requireContext(), R.drawable.baseline_pool_24)
//        firstMarker.setAnchor(Marker.ANCHOR_BOTTOM, Marker.ANCHOR_CENTER)
//        firstMarker.title = "Piscina"
//
//        var infoWindow = MarkerWindow(map, "Piscina")
//        firstMarker.infoWindow = infoWindow
//
//        //Añade el marcador al mapa
//        map.overlays.add(firstMarker)
//        //Actualiza el mapa
//        map.invalidate()
//
//        val secondMarker = Marker(map)
//        geoPoint = GeoPoint(37.15644086790343, -1.8265386272558004)
//
//        secondMarker.position = geoPoint
//        secondMarker.icon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_fav)
//        secondMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
//        secondMarker.title = "Bar"
//
//        infoWindow = MarkerWindow(map, "Bar")
//        secondMarker.infoWindow = infoWindow
//
//        //Añade el marcador al mapa
//        map.overlays.add(secondMarker)
//        //Actualiza el mapa
//        map.invalidate()

        //MyLocation
        val overlay: MyLocationNewOverlay = MyLocationNewOverlay(map)
        overlay.enableFollowLocation();
        overlay.enableMyLocation();
        map.overlays.add(overlay);
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment FragmentMaps.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            UbicationFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}