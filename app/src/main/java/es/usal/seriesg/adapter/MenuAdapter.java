package es.usal.seriesg.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import es.usal.seriesg.activity.R;
import es.usal.seriesg.application.SeriesGApplication;

/**
 * Created by nerko on 22/6/15.
 */
public class MenuAdapter extends ArrayAdapter<String> {

    private ImageView image1;
    private TextView text1;

    private Context context;
    private List<String> elements;
    private List<Integer> drawables;

    public MenuAdapter(Context context, List<String> elements, List<Integer> drawables) {
        super(context, -1, elements);
        this.context = context;
        this.elements = elements;
        this.drawables = drawables;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(this.context);
        convertView = inflater.inflate(R.layout.menu_row, parent, false);

        image1 = (ImageView) convertView.findViewById(R.id.image1);
        text1 = (TextView) convertView.findViewById(R.id.text1);

        image1.setImageDrawable(SeriesGApplication.getMResources().getDrawable(drawables.get(position), SeriesGApplication.getMTheme()));
        text1.setText(elements.get(position));
        return convertView;
    }


}
