export default function App() {
  return (
    <div className="min-h-screen bg-[#F5F9FC] text-[#16466A] flex items-center justify-center p-6">
      <div className="max-w-xl w-full bg-white border border-[#DCE7F0] border-t-4 border-t-[#38A9E8] rounded-lg p-7">
        <h1 className="text-2xl font-bold text-[#16466A] mb-2">INFOREPAIR</h1>
        <p className="text-sm text-[#64748B] mb-5">
          Le projet Java 17 / Spring Boot 3 / Maven / Thymeleaf / PostgreSQL complet a été généré dans le dossier <code className="bg-[#DDF3FF] px-1.5 py-0.5 rounded text-[#16466A]">inforepair/</code>.
        </p>
        <div className="bg-[#F5F9FC] border border-[#DCE7F0] rounded-md p-4 text-sm space-y-2 mb-5">
          <div><strong>Ouvrir dans NetBeans :</strong> sélectionnez <code className="text-[#16466A]">inforepair/pom.xml</code></div>
          <div><strong>Base de données :</strong> PostgreSQL (variables <code className="text-[#16466A]">DB_URL</code>, <code className="text-[#16466A]">DB_USERNAME</code>, <code className="text-[#16466A]">DB_PASSWORD</code>)</div>
          <div><strong>Déploiement :</strong> Dockerfile et instructions complètes dans <code className="text-[#16466A]">inforepair/README.md</code></div>
        </div>
        <a
          href="/inforepair.zip"
          download="inforepair.zip"
          className="inline-block bg-[#38A9E8] hover:bg-[#238EC9] text-white font-semibold text-sm px-4 py-2.5 rounded-md"
        >
          Télécharger l'archive ZIP du projet (inforepair.zip)
        </a>
      </div>
    </div>
  );
}
