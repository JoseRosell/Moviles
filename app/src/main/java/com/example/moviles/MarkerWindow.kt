package com.example.moviles


import android.widget.Button
import android.widget.TextView
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.infowindow.InfoWindow


class MarkerWindow(private val mapView: MapView, private val title: String): InfoWindow(R.layout.info_window, mapView)
{

    override fun onOpen(item: Any?)
    {
        // Following command
        closeAllInfoWindowsOn(mapView)

        val tvTitulo = mView.findViewById<TextView>(R.id.text_view_pin)
        tvTitulo.text = title

        val tvDesc = mView.findViewById<TextView>(R.id.text_view_descripcion)
        tvDesc.text = "Descripción de la actividad"

        val btn = mView.findViewById<Button>(R.id.close_button)

        btn.setOnClickListener {
            closeAllInfoWindowsOn(mapView)
        }
    }

    override fun onClose()
    {

    }

}