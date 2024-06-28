package mediwheel.co.mediwheelapp.Corporate.Adapters

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import med.Save.medsaveinv.DashBoradActivity
import med.Save.medsaveinv.InvestigationSearchActivity
import med.Save.medsaveinv.R
import med.Save.medsaveinv.models.fileList
import java.text.SimpleDateFormat
import java.util.Locale


class FileListAdapter(
    val arrayList: ArrayList<fileList>,
    var context: DashBoradActivity
) : RecyclerView.Adapter<FileListAdapter.ViewHolder>() {

    var mProgressDialog: ProgressDialog? = null

    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val tvPatienName: TextView = itemView.findViewById(R.id.tvPatienName)
        val tvPatienFileNo: TextView = itemView.findViewById(R.id.tvPatienFileNo)
        val tvProsperName: TextView = itemView.findViewById(R.id.tvProsperName)
        val tvAddmission: TextView = itemView.findViewById(R.id.tvAddmission)
        val tvHosName: TextView = itemView.findViewById(R.id.tvHosName)
        val tvHosCity: TextView = itemView.findViewById(R.id.tvHosCity)
        val llParent: LinearLayout = itemView.findViewById(R.id.llParent)


    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.custom_rec, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        mProgressDialog = ProgressDialog(context)
        mProgressDialog?.setTitle("Please wait...")
        mProgressDialog?.setCancelable(false)
        holder.tvPatienName.setText("Patient Name : " + arrayList.get(position).PatientName)
        holder.tvPatienFileNo.setText("File No : " + arrayList.get(position).FILENO)
        holder.tvProsperName.setText("Prosper Name : " + arrayList.get(position).Proposer)

        val dateString = arrayList[position].Admindate

        val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        val date = dateFormat.parse(dateString)
        val outputFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        val formattedDate = outputFormat.format(date)

        holder.tvAddmission.text = "Admission Date: $formattedDate"

       // holder.tvAddmission.setText("Admission Date : " + arrayList.get(position).Admindate)
        holder.tvHosName.setText("Hospital Name : " + arrayList.get(position).HospitalName)
        holder.tvHosName.setText("Hospital City : " + arrayList.get(position).HospAddress)

        holder.llParent.setOnClickListener {
            var dataFile = arrayList.get(position).FILENO
            //   Toast.makeText(context, "" + dataFile, Toast.LENGTH_SHORT).show()
            val intent = Intent(context, InvestigationSearchActivity::class.java)
            intent.putExtra("dataKey", dataFile)
            intent.putExtra("From", "Direct")
            context.startActivity(intent)
        }


    }


}







