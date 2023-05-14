import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.blank.InnerIndexActivity
import com.example.blank.PlayOCRActivity
import com.example.blank.R

class MainIndexItemAdapter(val itemlist:ArrayList<MainIndexItems>) : RecyclerView.Adapter<MainIndexItemAdapter.CustomViewHolder>()
{
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.main_index_item , parent, false)

        return CustomViewHolder(view)
    }
    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
        holder.itemTitle.text = itemlist.get(position).itemTitle
        holder.text_start_Inner_Index.text = itemlist.get(position).text_start_Inner_Index
        holder.text_start_Inner_Index.setOnClickListener {
            // 버튼 클릭 이벤트 처리
            val intent = Intent(holder.itemView.context, InnerIndexActivity::class.java)
            holder.itemView.context.startActivity(intent)
        }
        holder.btn_start_Play.setOnClickListener {
            // 버튼 클릭 이벤트 처리
            val intent = Intent(holder.itemView.context, PlayOCRActivity::class.java)
            holder.itemView.context.startActivity(intent)
        }
        holder.text_remove_list.setOnClickListener {
            // 버튼 클릭 이벤트 처리
            itemlist.removeAt(position)
            notifyItemRemoved(position)
        }
    }

    override fun getItemCount(): Int {
        return itemlist.size
    }

    class CustomViewHolder(itemview : View): RecyclerView.ViewHolder(itemview) {
        val itemTitle = itemView.findViewById<TextView>(R.id.itemTitle)
        val text_start_Inner_Index = itemView.findViewById<TextView>(R.id.text_start_Inner_Index)
        val btn_start_Play = itemView.findViewById<Button>(R.id.btn_start_Play)
        val text_remove_list = itemView.findViewById<TextView>(R.id.text_remove_list)
    }
}

